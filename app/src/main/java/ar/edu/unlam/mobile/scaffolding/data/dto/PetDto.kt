package ar.edu.unlam.mobile.scaffolding.data.dto

/**
 * Data Transfer Object (DTO) para Pet.
 *
 * Este modelo representa los datos tal como se almacenan en Firebase Firestore.
 * Es específico de la capa de datos y NO debe ser usado directamente en el dominio.
 *
 * Características:
 * - Constructor sin argumentos (requerido por Firebase)
 * - Usa enums (Type, Status, Gender) para interactuar con Firebase
 * - Incluye campos de ubicación (latitude, longitude)
 * - Se mapea a domain.model.Pet usando PetMapper
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
