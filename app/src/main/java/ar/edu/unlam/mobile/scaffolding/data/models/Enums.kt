package ar.edu.unlam.mobile.scaffolding.data.models

enum class Type(
    val label: String,
) {
    DOG("Perro"),
    CAT("Gato"),
    OTHER("Otro"),
}

enum class Status(
    val label: String,
) {
    LOST("Perdido"),
    FOUND("Encontrado"),
}

enum class Gender(
    val label: String,
) {
    MALE("Macho"),
    FEMALE("Hembra"),
    UNKNOWN("No lo sé"),
}
