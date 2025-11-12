package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Modelo de dominio para Pet.
 *
 * Representa una mascota en la capa de dominio con toda la información necesaria
 * para mostrar detalles y contactar al dueño.
 */
data class Pet(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val status: String = "",
    val gender: String = "",
    val seenAt: String = "",
    val locality: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val phoneNumber: String = "",
    val ownerId: String = "",
)
