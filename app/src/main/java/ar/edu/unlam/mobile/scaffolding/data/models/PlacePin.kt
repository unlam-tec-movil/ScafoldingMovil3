package ar.edu.unlam.mobile.scaffolding.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PlacePin(
    val id: String = java.util.UUID.randomUUID().toString(),
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val title: String = "",
    val snippet: String? = null
)
