package ar.edu.unlam.mobile.scaffolding.data.models

data class User(
    val email: String = "",
    val phone: String = "",
    val posts: List<String> = emptyList(), // LISTA CON EL ID DE LOS POSTS
    val id: String = "",
)
