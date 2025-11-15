package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Modelo de dominio para Pet.
 * Este modelo usa enums del dominio (Type, Status, Gender) para garantizar
 * type-safety en toda la aplicación.
 */
data class Pet(
    val id: String = "",
    val name: String = "",
    val type: Type = Type.OTHER,
    val status: Status = Status.LOST,
    val gender: Gender = Gender.MALE,
    val seenAt: String = "",
    val locality: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val phoneNumber: String = "",
    val ownerId: String = "",
)
