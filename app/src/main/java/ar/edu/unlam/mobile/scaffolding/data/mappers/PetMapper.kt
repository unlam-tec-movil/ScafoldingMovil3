package ar.edu.unlam.mobile.scaffolding.data.mappers

import ar.edu.unlam.mobile.scaffolding.data.dto.PetDto
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet as PetDomain

/**
 * Convierte un Pet de la capa de datos (DTO de Firebase) a un Pet de dominio.
 */
fun PetDto.toDomain(): PetDomain =
    PetDomain(
        id = this.id,
        name = this.name,
        type = this.type,
        status = this.status,
        gender = this.gender,
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
 */
fun PetDomain.toDto(): PetDto =
    PetDto(
        id = this.id,
        name = this.name,
        type = this.type,
        status = this.status,
        gender = this.gender,
        seenAt = this.seenAt,
        locality = this.locality,
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.imageUrl,
        phoneNumber = this.phoneNumber,
        ownerId = this.ownerId,
    )
