package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import kotlinx.coroutines.flow.Flow

/**
 * Puerto de salida.
 * - La implementación (PetsRepositoryImpl) se encarga de mapear entre capas
 */
interface PetsRepository {
    /**
     * Obtiene todas las mascotas.
     */
    fun getAllPets(): Flow<List<Pet>>

    /**
     * Obtiene mascotas por una lista de IDs.
     */
    fun getPetsByIds(ids: List<String>): Flow<List<Pet>>

    /**
     * Obtiene una mascota por su ID.
     */
    fun getPetById(petId: String): Flow<Pet?>

    /**
     * Guarda una mascota.
     */
    suspend fun savePet(pet: Pet): String
}
