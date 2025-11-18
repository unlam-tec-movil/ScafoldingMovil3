package ar.edu.unlam.mobile.scaffolding.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para un "leg" (es un segmento) de la ruta.
 *
 * Contiene información de distancia y duración del segmento.
 */
data class LegDto(
    @SerializedName("distance")
    val distance: DistanceDto,
    @SerializedName("duration")
    val duration: DurationDto
)
