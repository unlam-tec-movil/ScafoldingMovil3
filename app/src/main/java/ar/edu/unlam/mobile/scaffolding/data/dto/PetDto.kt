package ar.edu.unlam.mobile.scaffolding.data.dto

import ar.edu.unlam.mobile.scaffolding.domain.model.Gender
import ar.edu.unlam.mobile.scaffolding.domain.model.Status
import ar.edu.unlam.mobile.scaffolding.domain.model.Type

/**
 * DTO para "Pet" en Firebase Firestore.
 * Representa el modelo de la base de datos, se mapea al dominio "Pet".
 */
data class PetDto(
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
