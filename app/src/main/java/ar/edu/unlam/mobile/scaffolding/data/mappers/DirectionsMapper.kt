package ar.edu.unlam.mobile.scaffolding.data.mappers

import ar.edu.unlam.mobile.scaffolding.data.dto.DirectionsResponseDto
import ar.edu.unlam.mobile.scaffolding.domain.model.Route
import com.google.maps.android.PolyUtil

/**
 * Mapper para convertir DirectionsResponseDto (de Data) a Route (de Domain).
 *
 * Extraer los datos anidados del DTO de Google Directions
 * Decodificar la polyline codificada de la lista de LatLng
 * Mapear al modelo de dominio de la app (que está simplificado)
 */

fun DirectionsResponseDto.toDomain(): Route {
    // Validar que hay al menos una ruta
    if (routes.isEmpty()) {
        throw IllegalStateException("La respuesta no contiene rutas")
    }

    // Obtener la primera ruta (ya que Google puede devolver alternativas, pero solo usamos la primera)
    val firstRoute = routes[0]

    // Validar que la ruta tiene al menos un segmento (leg)
    if (firstRoute.legs.isEmpty()) {
        throw IllegalStateException("La ruta no contiene segmenos (legs)")
    }

    // Obtener el primer segmento
    val firstLeg = firstRoute.legs[0]

    // Extraer distancia
    val distanceMeters = firstLeg.distance.value
    val distanceText = firstLeg.distance.text

    // Extraer duración
    val durationSeconds = firstLeg.duration.value
    val durationText = firstLeg.duration.text

    // Decodificar la polyline, Google codifica los puntos en un string especial para ahorrar espacio
    // Entonces un string representa múltiples coordenadas
    val encodedPolyline = firstRoute.overviewPolyline.points
    val decodedPoints = PolyUtil.decode(encodedPolyline)

    // Crear el modelo de dominio limpio
    return Route(
        points = decodedPoints,
        distanceMeters = distanceMeters,
        durationSeconds = durationSeconds,
        distanceText = distanceText,
        durationText = durationText
    )
}
