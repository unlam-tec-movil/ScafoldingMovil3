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
)

// data class Status(
//    val description: String,
//    val id: Integer,
// )
//
// data class Type(
//    val name: String,
//    val id: Integer,
// )

// data class Color(
//    val name: String,
//    val id: Integer,
// )
//
// data class Hair(
//    val name: String,
//    val id: Integer,
// )
//
// data class gender(
//    val description: String,
//    val id: Integer,
// )
