package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Modelo de dominio que representa la ubicación del usuario.
 * Tiene la Latitud y Longitud en grados decimales.
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double
)
