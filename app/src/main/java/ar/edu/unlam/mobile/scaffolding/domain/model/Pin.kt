package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Modelo de dominio del pin en el mapa.
 */
data class Pin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String? = null,
)
