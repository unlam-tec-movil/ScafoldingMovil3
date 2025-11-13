package ar.edu.unlam.mobile.scaffolding.ui.screens.posts

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.dto.Gender
import ar.edu.unlam.mobile.scaffolding.data.dto.PetDto
import ar.edu.unlam.mobile.scaffolding.data.dto.Status
import ar.edu.unlam.mobile.scaffolding.data.dto.TipoDePublicacion
import ar.edu.unlam.mobile.scaffolding.data.dto.Type
import ar.edu.unlam.mobile.scaffolding.data.dto.User
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDto
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class PostViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val petsRepository: PetsRepository,
    private val storage: FirebaseStorage,
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    // NOTA: PostViewModel mantiene Pet de data.models (PetDto) para la UI (formularios)
    // y usa mappers al interactuar con el repository (que trabaja con domain.model.Pet)
    private val _pets = MutableStateFlow<List<PetDto>>(emptyList())
    val pets: StateFlow<List<PetDto>> = _pets

    private val _statusFilter = MutableStateFlow(Status.LOST)
    private val _typeFilter = MutableStateFlow<Type?>(null)
    private val _genderFilter = MutableStateFlow<Gender?>(null)
    private val _localityFilter = MutableStateFlow<String?>(null)

    val statusFilter = _statusFilter.asStateFlow()
    val typeFilter = _typeFilter.asStateFlow()
    val genderFilter = _genderFilter.asStateFlow()
    val localityFilter = _localityFilter.asStateFlow()

    private val _postTipo = MutableStateFlow<TipoDePublicacion?>(null)
    val postTipo = _postTipo.asStateFlow()

    fun setPostTipo(tipoPublicacion: TipoDePublicacion) {
        _postTipo.value = tipoPublicacion
    }

    val filteredPets: StateFlow<List<PetDto>> =
        combine(
            _pets,
            _statusFilter,
            _typeFilter,
            _genderFilter,
            _localityFilter,
        ) { pets, status, type, gender, locality ->
            pets.filter { pet ->
                pet.status == status &&
                    (type == null || pet.type == type) &&
                    (gender == null || pet.gender == gender) &&
                    (
                        locality.isNullOrBlank() ||
                            pet.locality.equals(
                                locality,
                                ignoreCase = true,
                            )
                        )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadCurrentUser()
        loadAllPets()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userRepository.getCurrentUser()
        }
    }

    private fun loadAllPets() {
        viewModelScope.launch {
            petsRepository
                .getAllPets()
                .map { domainPets ->
                    // Convertir de domain.model.Pet → data.models.Pet (para la UI)
                    domainPets.map { it.toDto() }
                }.collect { list ->
                    _pets.value = list
                }
        }
    }

    fun setStatusFilter(status: Status) {
        _statusFilter.value = status
    }

    fun setTypeFilter(type: Type?) {
        _typeFilter.value = type
    }

    fun setGenderFilter(gender: Gender?) {
        _genderFilter.value = gender
    }

    fun setLocalityFilter(locality: String?) {
        _localityFilter.value = locality
    }

    fun clearFilters() {
        _typeFilter.value = null
        _genderFilter.value = null
        _localityFilter.value = null
    }

    fun savePet(
        pet: PetDto, // Recibe Pet de data.models (desde el formulario)
        imageUri: Uri?,
        onSuccessMessage: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val user = _currentUser.value ?: return@launch

                var finalPet =
                    pet.copy(
                        ownerId = user.id,
                        phoneNumber = user.phone,
                    )

                if (imageUri != null) {
                    val url = uploadImage(imageUri)
                    finalPet = finalPet.copy(imageUrl = url)
                }

                // Convertir de data.models.Pet → domain.model.Pet antes de guardar
                val domainPet = finalPet.toDomain()
                val petId = petsRepository.savePet(domainPet)

                userRepository.addPostToUser(user.id, petId)

                onSuccessMessage("Mascota registrada con éxito")
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error guardando mascota: ${e.message}")
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        suspendCoroutine { cont ->
            val ref = storage.reference.child("pets/${UUID.randomUUID()}.jpg")

            ref
                .putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener { cont.resume(it.toString()) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }.addOnFailureListener { cont.resumeWithException(it) }
        }
}

//
// @HiltViewModel
// class PostViewModel @Inject constructor(
//    private val userRepository: UserRepository,
//    private val db: FirebaseFirestore,
//    private val storage: FirebaseStorage
// ) : ViewModel() {
//
//    private val _statusFilter = MutableStateFlow(Status.LOST)
//    val statusFilter = _statusFilter.asStateFlow()
//
//    private val _typeFilter = MutableStateFlow<Type?>(null)
//    private val _genderFilter = MutableStateFlow<Gender?>(null)
//    private val _localityFilter = MutableStateFlow<String?>(null)
//
//    val typeFilter = _typeFilter.asStateFlow()
//    val genderFilter = _genderFilter.asStateFlow()
//    val localityFilter = _localityFilter.asStateFlow()
//
//    private val _currentUser = MutableStateFlow<User?>(null)
//    val currentUser = _currentUser.asStateFlow()
//
//    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
//    val pets: StateFlow<List<Pet>> = _pets
//
//    private val _postTipo = MutableStateFlow<TipoDePublicacion?>(null)
//    val postTipo = _postTipo.asStateFlow()
//
//    fun setPostTipo(tipoPublicacion: TipoDePublicacion) {
//        _postTipo.value = tipoPublicacion
//    }
//
//
//    val filteredPets: StateFlow<List<Pet>> =
//        combine(
//            _pets,
//            _statusFilter,
//            _typeFilter,
//            _genderFilter,
//            _localityFilter
//        ) { pets, status, type, gender, locality ->
//            pets.filter { pet ->
//                (pet.status == status) &&
//                        (type == null || pet.type == type) &&
//                        (gender == null || pet.gender == gender) &&
//                        (locality.isNullOrBlank() || pet.locality.equals(locality, ignoreCase = true))
//            }
//        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
//
//    init {
//        viewModelScope.launch {
//            _currentUser.value = userRepository.getCurrentUser()
//        }
//        getAllPets()
//    }
//
//    fun setStatusFilter(status: Status) { _statusFilter.value = status }
//    fun setTypeFilter(type: Type?) { _typeFilter.value = type }
//    fun setGenderFilter(gender: Gender?) { _genderFilter.value = gender }
//    fun setLocalityFilter(locality: String?) { _localityFilter.value = locality }
//
//    fun clearFilters() {
//        _typeFilter.value = null
//        _genderFilter.value = null
//        _localityFilter.value = null
//    }
//
//    fun getAllPets() {
//        db.collection("Pets")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.e("PostViewModel", "Error al obtener mascotas: ${e.message}")
//                    return@addSnapshotListener
//                }
//
//                if (snapshot != null) {
//                    val petsList = snapshot.documents.mapNotNull { doc ->
//                        doc.toObject(Pet::class.java)?.copy(id = doc.id)
//                    }
//                    _pets.value = petsList
//                }
//            }
//    }
//
//    fun savePet(
//        pet: Pet,
//        imageUri: Uri?,
//        onSuccessMessage: (String) -> Unit
//    ) {
//        val phone = _currentUser.value?.phone ?: ""
//        val petWithPhone = pet.copy(phoneNumber = phone)
//
//        if (imageUri != null) {
//            val storageRef = storage.reference.child("pets/${UUID.randomUUID()}.jpg")
//
//            storageRef.putFile(imageUri)
//                .addOnSuccessListener {
//                    storageRef.downloadUrl
//                        .addOnSuccessListener { uri ->
//                            val petWithImage = petWithPhone.copy(imageUrl = uri.toString())
//                            uploadPetToFirestore(petWithImage, onSuccessMessage)
//                        }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("PostViewModel", "Error subiendo imagen: ${e.message}")
//                }
//        } else {
//            uploadPetToFirestore(petWithPhone, onSuccessMessage)
//        }
//    }
//
//
//    private fun uploadPetToFirestore(
//        pet: Pet,
//        onSuccessMessage: (String) -> Unit
//    ) {
//        db.collection("Pets")
//            .add(pet)
//            .addOnSuccessListener { docRef ->
//                Log.d("PostViewModel", "Mascota guardada con ID: ${docRef.id}")
//                onSuccessMessage("Mascota registrada con éxito")
//            }
//            .addOnFailureListener { e ->
//                Log.e("PostViewModel", "Error guardando mascota: ${e.message}")
//            }
//    }
// }
