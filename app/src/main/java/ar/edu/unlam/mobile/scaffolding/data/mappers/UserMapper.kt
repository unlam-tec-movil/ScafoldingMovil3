package ar.edu.unlam.mobile.scaffolding.data.mappers

import ar.edu.unlam.mobile.scaffolding.data.dto.UserDto
import ar.edu.unlam.mobile.scaffolding.domain.model.User

/**
 * Convierte un UserDto (capa data) a User (capa domain).
 * Esta función se usa cuando se leen datos de Firebase y se necesitan
 * convertir al modelo de dominio.
 */
fun UserDto.toDomain(): User =
    User(
        id = id,
        email = email,
        phone = phone,
        postIds = posts,
    )

/**
 * Convierte un User (capa domain) a UserDto (capa data).
 * Esta función se usa cuando se necesita guardar un usuario en Firebase.
 */
fun User.toDto(): UserDto =
    UserDto(
        id = id,
        email = email,
        phone = phone,
        posts = postIds,
    )
