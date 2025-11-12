package ar.edu.unlam.mobile.scaffolding.data.dto

/**
 * Data Transfer Object (DTO) para User.
 *
 * Representa los datos de usuario tal como se almacenan en Firebase Firestore.
 */
data class User(
    val email: String = "",
    val phone: String = "",
    val posts: List<String> = emptyList(), // LISTA CON EL ID DE LOS POSTS
    val id: String = "",
)
