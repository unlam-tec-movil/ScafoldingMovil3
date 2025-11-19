package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import kotlinx.coroutines.flow.Flow

interface PetsRepository {
    fun getAllPets(): Flow<List<Pet>>
    fun getPetsByIds(ids: List<String>): Flow<List<Pet>>
    fun getPetById(petId: String): Flow<Pet?>
    suspend fun savePet(pet: Pet): String   // 👉 devuelve el ID del pet guardado
    fun getPetsByUser(userId: String): Flow<List<Pet>>
}
