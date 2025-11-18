package ar.edu.unlam.mobile.scaffolding.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de Google Directions API.
 * Es la estructura JSON que devuelve Google cuando pedimos una ruta.
 */
data class DirectionsResponseDto(
    @SerializedName("routes")
    val routes: List<RouteDto>,
    @SerializedName("status")
    val status: String,
)
