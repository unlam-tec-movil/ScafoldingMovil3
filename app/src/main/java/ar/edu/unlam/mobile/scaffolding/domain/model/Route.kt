package ar.edu.unlam.mobile.scaffolding.domain.model

import com.google.android.gms.maps.model.LatLng

/**
 * Modelo de dominio que representa una ruta entre dos puntos.
 *
 * @property points Lista de coordenadas que forman la polilínea azul.
 * @property distanceMeters Distancia total de la ruta en metros.
 * @property durationSeconds Duración estimada del recorrido en segundos.
 * @property distanceText Distancia formateada para mostrar.
 * @property durationText Duración formateada para mostrar.
 */
data class Route(
    val points: List<LatLng>,
    val distanceMeters: Int,
    val durationSeconds: Int,
    val distanceText: String,
    val durationText: String,
)
