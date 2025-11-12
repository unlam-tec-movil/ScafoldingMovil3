package ar.edu.unlam.mobile.scaffolding.data.mappers

import ar.edu.unlam.mobile.scaffolding.data.dto.Gender
import ar.edu.unlam.mobile.scaffolding.data.dto.PetDto
import ar.edu.unlam.mobile.scaffolding.data.dto.Status
import ar.edu.unlam.mobile.scaffolding.data.dto.Type
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet as PetDomain

/**
 * Convierte un Pet de la capa de datos (DTO de Firebase) a un Pet de dominio.
 *
 * Transformaciones:
 * - Type (enum) → String
 * - Status (enum) → String
 * - Gender (enum) → String
 */
fun PetDto.toDomain(): PetDomain =
    PetDomain(
        id = this.id,
        name = this.name,
        type = this.type.label,
        status = this.status.label,
        gender = this.gender.label,
        seenAt = this.seenAt,
        locality = this.locality,
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.imageUrl,
        phoneNumber = this.phoneNumber,
        ownerId = this.ownerId,
    )

/**
 * Convierte un Pet del dominio a un Pet de la capa de datos (DTO para Firebase).
 *
 * Transformaciones:
 * - String → Type (enum) - busca por label
 * - String → Status (enum) - busca por label
 * - String → Gender (enum) - busca por label
 *
 * Nota: Si no encuentra el enum correspondiente, usa un valor por defecto.
 */
fun PetDomain.toDto(): PetDto =
    PetDto(
        id = this.id,
        name = this.name,
        type = Type.entries.find { it.label == this.type } ?: Type.OTHER,
        status = Status.entries.find { it.label == this.status } ?: Status.LOST,
        gender = Gender.entries.find { it.label == this.gender } ?: Gender.UNKNOWN,
        seenAt = this.seenAt,
        locality = this.locality,
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.imageUrl,
        phoneNumber = this.phoneNumber,
        ownerId = this.ownerId,
    )
