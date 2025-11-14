package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.dto.PetDto
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDto
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet as PetDomain

/**
 * Implementación del repositorio de mascotas.
 *
 * Responsabilidad:
 * - Conecta con Firebase Firestore
 * - Convierte entre PetDto (Firebase) y Pet (dominio) usando mappers
 * - Implementa la interfaz PetsRepository del dominio
 */
class PetsRepositoryImpl
    @Inject
    constructor(
        private val db: FirebaseFirestore,
    ) : PetsRepository {
        override fun getAllPets(): Flow<List<PetDomain>> =
            db
                .collection("Pets")
                .snapshots()
                .map { snap ->
                    snap.documents.mapNotNull { doc ->
                        doc
                            .toObject(PetDto::class.java)
                            ?.copy(id = doc.id)
                            ?.toDomain() // DTO → Domain
                    }
                }

        override fun getPetsByIds(ids: List<String>): Flow<List<PetDomain>> =
            if (ids.isEmpty()) {
                flow { emit(emptyList()) }
            } else {
                db
                    .collection("Pets")
                    .whereIn(FieldPath.documentId(), ids)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(PetDto::class.java).map { it.toDomain() } // DTO → Domain
                    }
            }

        override fun getPetById(petId: String): Flow<PetDomain?> =
            db
                .collection("Pets")
                .document(petId)
                .snapshots()
                .map { doc ->
                    doc
                        .toObject(PetDto::class.java)
                        ?.copy(id = doc.id)
                        ?.toDomain() // DTO → Domain
                }

        override suspend fun savePet(pet: PetDomain): String {
            val docRef = db.collection("Pets").document()
            val petDto = pet.toDto().copy(id = docRef.id) // Domain → DTO

            docRef.set(petDto).await()

            return docRef.id
        }
    }
