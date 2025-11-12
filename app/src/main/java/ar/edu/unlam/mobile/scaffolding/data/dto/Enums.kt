package ar.edu.unlam.mobile.scaffolding.data.dto

/**
 * Enums utilizados en los DTOs de Firebase.
 *
 * Estos enums representan los valores tal como se almacenan en Firestore.
 */

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

enum class TipoDePublicacion {
    MASCOTAPERDIDA,
    MASCOTAENCONTRADA,
}
