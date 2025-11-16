package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Están en la capa de Dominio porque:
 * - Son conceptos del negocio (tipo de mascota, estado, género)
 * - No tienen dependencias externas (es Kotlin puro)
 * - Mantiene type-safety en toda la app
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
