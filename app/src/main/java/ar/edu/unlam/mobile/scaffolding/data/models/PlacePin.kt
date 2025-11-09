package ar.edu.unlam.mobile.scaffolding.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PlacePin(
    val id: String =
        java.util.UUID
            .randomUUID()
            .toString(),
    val lat: Double,
    val lng: Double,
    val imageURL: String,
)
