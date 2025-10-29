package ar.edu.unlam.mobile.scaffolding.data.models

data class Pet(
    val name: String,
    val type: Type,
    val status: Status,
    val color: String,
    val hair: String,
)

data class Status(
    val description: String,
    val id: Integer,
)

data class Type(
    val name: String,
    val id: Integer,
)

data class Color(
    val name: String,
    val id: Integer,
)

data class Hair(
    val name: String,
    val id: Integer,
)
