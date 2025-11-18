package ar.edu.unlam.mobile.scaffolding.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para el objeto "distance".
 *
 * Google devuelve la distancia como valor numérico en metros en el "value",
 * y como formato legible (ejemplo: 1.2km) en el "text".
 */
data class DistanceDto(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)
