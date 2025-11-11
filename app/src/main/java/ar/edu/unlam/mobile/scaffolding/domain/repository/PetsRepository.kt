package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import kotlinx.coroutines.flow.Flow

interface PetsRepository {
    fun getAllPets(): Flow<List<Pet>>

    fun getPetsByIds(ids: List<String>): Flow<List<Pet>>

    suspend fun savePet(pet: Pet): String
}
