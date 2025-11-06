package ar.edu.unlam.mobile.scaffolding.data.models

data class User(
    val name: String = "",
    val userId: String = "",
    val phone: String = "",
    val password: String = "",
    val posts: List<String> = emptyList(), //LISTA CON EL ID DE LOS POSTS
    //val id: String,
    //val email: String,
)
