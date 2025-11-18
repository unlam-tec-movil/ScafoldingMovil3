package ar.edu.unlam.mobile.scaffolding.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para el objeto "polyline".
 *
 * Google codifica las coordenadas de la ruta en un string especial
 * usando un algoritmo.
 * Ese string comprimido representa muchos puntos LatLng.
 * Hay que decodificar ese string para obtener las coordenadas reales.
 * Se tiene que decodificar usando PolyUtil.decode(points).
 */
data class PolylineDto(
    @SerializedName("points")
    val points: String,
)
