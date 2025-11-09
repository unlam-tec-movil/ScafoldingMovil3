package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Modelo de dominio del pin en el mapa.
 */
data class Pin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String
)

interface PetRepository {
    suspend fun getPetSpots(): List<Pin>
}
