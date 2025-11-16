package ar.edu.unlam.mobile.scaffolding.ui.screens.posts

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.Gender
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.domain.model.Status
import ar.edu.unlam.mobile.scaffolding.domain.model.TipoDePublicacion
import ar.edu.unlam.mobile.scaffolding.domain.model.Type
import ar.edu.unlam.mobile.scaffolding.domain.model.User
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

        // PostViewModel expone Pet del dominio a la UI (no DTOs)
        // Esto respeta Clean Architecture: UI → Domain (sin conocer detalles de Data)
        private val _pets = MutableStateFlow<List<Pet>>(emptyList())
        val pets: StateFlow<List<Pet>> = _pets

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

        // ===== ESTADO DE UBICACIÓN SELECCIONADA EN EL MAPA =====

        /**
         * Latitud seleccionada por el usuario en MapPostScreen.
         * null: el usuario aún no seleccionó una ubicación.
         */
        private val _selectedLatitude = MutableStateFlow<Double?>(null)
        val selectedLatitude = _selectedLatitude.asStateFlow()

        /**
         * Longitud seleccionada por el usuario en MapPostScreen.
         * null: el usuario aún no seleccionó una ubicación.
         */
        private val _selectedLongitude = MutableStateFlow<Double?>(null)
        val selectedLongitude = _selectedLongitude.asStateFlow()

        fun setPostTipo(tipoPublicacion: TipoDePublicacion) {
            _postTipo.value = tipoPublicacion
        }

        /**
         * Guarda la ubicación seleccionada por el usuario en el mapa.
         *
         * Esta función es llamada por MapPostScreen cuando el usuario
         * confirma la ubicación en el diálogo.
         */
        fun setSelectedLocation(
            latitude: Double,
            longitude: Double,
        ) {
            _selectedLatitude.value = latitude
            _selectedLongitude.value = longitude
        }

        val filteredPets: StateFlow<List<Pet>> =
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
                // Ahora la UI trabaja directamente con modelos del dominio
                // No necesitamos conversión a DTO
                petsRepository
                    .getAllPets()
                    .collect { domainPets ->
                        _pets.value = domainPets
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
            pet: Pet, // Recibe Pet del dominio (desde el formulario)
            imageUri: Uri?,
            onSuccessMessage: (String) -> Unit,
        ) {
            viewModelScope.launch {
                try {
                    val user = _currentUser.value ?: return@launch

                    // Obtener la ubicación seleccionada en MapPostScreen
                    val latitude = _selectedLatitude.value ?: 0.0
                    val longitude = _selectedLongitude.value ?: 0.0

                    var finalPet =
                        pet.copy(
                            ownerId = user.id,
                            phoneNumber = user.phone,
                            latitude = latitude, // Agregar ubicación del mapa
                            longitude = longitude, // Agregar ubicación del mapa
                        )

                    if (imageUri != null) {
                        val url = uploadImage(imageUri)
                        finalPet = finalPet.copy(imageUrl = url)
                    }

                    // Ya no necesitamos conversión, finalPet ya es del dominio
                    val petId = petsRepository.savePet(finalPet)

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
