package ar.edu.unlam.mobile.scaffolding.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para el objeto "duration".
 *
 * Google devuelve la duración en dos formatos: en text y en value.
 * - text se utiliza para mostrar la duración formateada para mostrar al usuario.
 * - value es el valor en segundos, se puede utilizar para cálculos.
 */
data class DurationDto(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int,
)
