package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PetsRepositoryImpl
    @Inject
    constructor(
        private val db: FirebaseFirestore,
    ) : PetsRepository {
        override fun getAllPets(): Flow<List<Pet>> =
            db
                .collection("Pets")
                .snapshots()
                .map { snap ->
                    snap.documents.mapNotNull { doc ->
                        doc.toObject(Pet::class.java)?.copy(id = doc.id)
                    }
                }

        override fun getPetsByIds(ids: List<String>): Flow<List<Pet>> =
            if (ids.isEmpty()) {
                flow { emit(emptyList()) }
            } else {
                db
                    .collection("Pets")
                    .whereIn(FieldPath.documentId(), ids)
                    .snapshots()
                    .map { it.toObjects(Pet::class.java) }
            }

        override suspend fun savePet(pet: Pet): String {
            val docRef = db.collection("Pets").document()
            val petWithId = pet.copy(id = docRef.id)

            docRef.set(petWithId).await()

            return docRef.id
        }
    }
