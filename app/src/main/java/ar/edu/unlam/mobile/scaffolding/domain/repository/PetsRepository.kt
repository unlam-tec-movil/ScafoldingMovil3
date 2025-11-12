package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de mascotas (capa de dominio).
 *
 * IMPORTANTE:
 * - Esta interfaz está en la capa de DOMINIO
 * - Trabaja SOLO con modelos de dominio (domain.model.Pet)
 * - NO debe conocer detalles de Firebase o la capa de datos
 * - La implementación (PetsRepositoryImpl) se encarga de mapear entre capas
 */
interface PetsRepository {
    /**
     * Obtiene todas las mascotas.
     * @return Flow con lista de Pets del dominio.
     */
    fun getAllPets(): Flow<List<Pet>>

    /**
     * Obtiene mascotas por una lista de IDs.
     * @param ids Lista de IDs de mascotas.
     * @return Flow con lista de Pets del dominio.
     */
    fun getPetsByIds(ids: List<String>): Flow<List<Pet>>

    /**
     * Obtiene una mascota por su ID.
     * @param petId ID de la mascota.
     * @return Flow con Pet del dominio (null si no existe).
     */
    fun getPetById(petId: String): Flow<Pet?>

    /**
     * Guarda una mascota.
     * @param pet Mascota del dominio a guardar.
     * @return ID de la mascota guardada.
     */
    suspend fun savePet(pet: Pet): String
}
