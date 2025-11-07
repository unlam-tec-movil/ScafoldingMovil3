package ar.edu.unlam.mobile.scaffolding.domain.model
data class Pet(
    val id: String = "",
    val name: String = "",
    val type: String = "", // TODO: Cambiar a Type (enum) cuando esté listo
    val status: String = "", // TODO: Cambiar a Status (enum) cuando esté listo
    val gender: String = "",
    val seenAt: String = "",
    val locality: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = ""
)
