package ar.edu.unlam.mobile.scaffolding.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para un elemento de la lista "routes".
 *
 * Cada ruta contiene:
 *  -legs: Segmentos de la ruta (distancia, duración, pasos)
 *  -overview_polyline: Polilínea codificada de toda la ruta
 */
data class RouteDto(
    @SerializedName("legs")
    val legs: List<LegDto>,
    @SerializedName("overview_polyline")
    val overviewPolyline: PolylineDto
)
