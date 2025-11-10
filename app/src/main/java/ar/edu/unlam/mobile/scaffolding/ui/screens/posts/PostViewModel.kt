package ar.edu.unlam.mobile.scaffolding.ui.screens.posts
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.models.Gender
import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import ar.edu.unlam.mobile.scaffolding.data.models.Status
import ar.edu.unlam.mobile.scaffolding.data.models.Type
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

@HiltViewModel
class PostViewModel
    @Inject
    constructor() : ViewModel() {
        private val db = Firebase.firestore
        private val storage = Firebase.storage

        private val _statusFilter = MutableStateFlow(Status.LOST)
        val statusFilter = _statusFilter.asStateFlow()

        private val _typeFilter = MutableStateFlow<Type?>(null)
        private val _genderFilter = MutableStateFlow<Gender?>(null)
        private val _localityFilter = MutableStateFlow<String?>(null)

        val typeFilter = _typeFilter.asStateFlow()
        val genderFilter = _genderFilter.asStateFlow()
        val localityFilter = _localityFilter.asStateFlow()

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
        val pets: StateFlow<List<Pet>> = _pets

    val filteredPets: StateFlow<List<Pet>> =
        combine(
            _pets,
            _statusFilter,
            _typeFilter,
            _genderFilter,
            _localityFilter
        ) { pets, status, type, gender, locality ->
            pets.filter { pet ->
                (pet.status == status) &&
                        (type == null || pet.type == type) &&
                        (gender == null || pet.gender == gender) &&
                        (locality.isNullOrBlank() || pet.locality.equals(locality, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    init {
            getAllPets() // cuando se crea el ViewModel, carga los posts
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

    fun getAllPets() {
            db
                .collection("Pets")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("PostViewModel", "Error al obtener mascotas: ${e.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val petsList =
                            snapshot.documents.mapNotNull { doc ->
                                doc.toObject(Pet::class.java)?.copy(id = doc.id)
                            }
                        _pets.value = petsList
                    }
                }
        }

        fun savePet(
            pet: Pet,
            imageUri: Uri?,
            onSuccessMessage: (String) -> Unit,
        ) {
            Log.d("PostViewModel", "Guardando mascota...")

            if (imageUri != null) {
                val storageRef = storage.reference.child("pets/${UUID.randomUUID()}.jpg")
                Log.d("PostViewModel", "Subiendo imagen: $imageUri")

                storageRef
                    .putFile(imageUri)
                    .addOnSuccessListener {
                        Log.d("PostViewModel", "Imagen subida correctamente.")
                        storageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                Log.d("PostViewModel", "URL de imagen: $uri")

                                val petWithImage = pet.copy(imageUrl = uri.toString())
                                uploadPetToFirestore(petWithImage, onSuccessMessage)
                            }.addOnFailureListener { e ->
                                Log.e("PostViewModel", "Error obteniendo URL: ${e.message}")
                            }
                    }.addOnFailureListener { e ->
                        Log.e("PostViewModel", "Error subiendo imagen: ${e.message}")
                    }
            } else {
                Log.d("PostViewModel", "Sin imagen, guardando directamente en Firestore.")
                uploadPetToFirestore(pet, onSuccessMessage)
            }
        }

        private fun uploadPetToFirestore(
            pet: Pet,
            onSuccessMessage: (String) -> Unit,
        ) {
            Log.d("PostViewModel", "Subiendo mascota a Firestore: $pet")
            db
                .collection("Pets")
                .add(pet)
                .addOnSuccessListener { docRef ->
                    Log.d("PostViewModel", "Mascota guardada con ID: ${docRef.id}")
                    onSuccessMessage("Mascota registrada con éxito")
                }.addOnFailureListener { e ->
                    Log.e("PostViewModel", "Error guardando mascota: ${e.message}")
                }
        }

    }
