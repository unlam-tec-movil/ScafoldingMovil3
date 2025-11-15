package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Modelo de dominio para User.
 */
data class User(
    val id: String,
    val email: String,
    val phone: String,
    val postIds: List<String>,
)
