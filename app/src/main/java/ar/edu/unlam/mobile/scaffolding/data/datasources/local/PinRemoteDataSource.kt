package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import ar.edu.unlam.mobile.scaffolding.data.models.PlacePin
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// ESTA CLASE SUBE LA INFO DE LOS PINS A FIRESTORE PARA QUE LA VEAN TODOS LOS USUARIOS, NO PODEMOS USAR
// DATASTORE PARA ESTO.

class PinRemoteDataSource
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) {
        private val pinsCollection = firestore.collection("pins")

        fun observePins(): Flow<List<PlacePin>> =
            callbackFlow {
                val listener =
                    pinsCollection.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val list = snapshot?.toObjects(PlacePin::class.java) ?: emptyList()
                        trySend(list)
                    }

                awaitClose { listener.remove() }
            }

        suspend fun getAllPins(): List<PlacePin> =
            try {
                val snapshot = pinsCollection.get().await()
                snapshot.toObjects(PlacePin::class.java)
            } catch (e: Exception) {
                emptyList()
            }

        suspend fun savePins(pins: List<PlacePin>): Boolean =
            try {
                // Borro la colección completa y vuelvo a cargar todo
                val batch = firestore.batch()

                // Paso 1: Obtener los docs existentes para borrarlos
                val existing = pinsCollection.get().await()
                existing.documents.forEach { batch.delete(it.reference) }

                // Paso 2: Guardar los nuevos
                pins.forEach { pin ->
                    val docRef = pinsCollection.document(pin.id)
                    batch.set(docRef, pin)
                }

                batch.commit().await()
                true
            } catch (e: Exception) {
                false
            }
    }
