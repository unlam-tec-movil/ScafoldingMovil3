package ar.edu.unlam.mobile.scaffolding.data.dto

/**
 * DTO para "User" en Firebase Firestore.
 * Se mapea al modelo de dominio "User".
 */
data class UserDto(
    val email: String = "",
    val phone: String = "",
    val posts: List<String> = emptyList(), // LISTA CON EL ID DE LOS POSTS
    val id: String = "",
)
