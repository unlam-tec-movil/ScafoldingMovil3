package ar.edu.unlam.mobile.scaffolding.data.models

// Esto sería la info de los posts.
data class Pet(
    val id: String = "",
    val name: String = "",
    val type: Type = Type.OTHER,
    val status: Status = Status.LOST,
    val gender: Gender = Gender.MALE,
    val seenAt: String = "",
    val locality: String = "",
    val imageUrl: String = "",
    val phoneNumber: String = "",
    val ownerId: String = "",
)
