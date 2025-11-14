package ar.edu.unlam.mobile.scaffolding.domain.usecase

import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Caso de uso que calcula el bearing (ángulo/rumbo) desde una ubicación origen
 * hacia una ubicación destino.
 *
 * El bearing es el ángulo en grados (0-360) medido desde el Norte en sentido horario:
 * - 0° = Norte
 * - 90° = Este
 * - 180° = Sur
 * - 270° = Oeste
 *
 * Este Use Case contiene LÓGICA DE NEGOCIO PURA (cálculo trigonométrico),
 * por lo que se justifica su existencia
 */
class CalculateBearingUseCase
    @Inject
    constructor() {
        /**
         * Calcula el bearing desde la ubicación del usuario hacia la ubicación de la mascota.
         *
         * Usa la fórmula de bearing esférico (great circle bearing) que tiene en cuenta
         * la curvatura de la Tierra.
         *
         * @param fromLatitude Latitud del punto origen (usuario) en grados decimales.
         * @param fromLongitude Longitud del punto origen (usuario) en grados decimales.
         * @param toLatitude Latitud del punto destino (mascota) en grados decimales.
         * @param toLongitude Longitud del punto destino (mascota) en grados decimales.
         * @return Bearing en grados (0-360).
         */
        operator fun invoke(
            fromLatitude: Double,
            fromLongitude: Double,
            toLatitude: Double,
            toLongitude: Double,
        ): Float {
            // Convertir grados a radianes
            val lat1 = Math.toRadians(fromLatitude)
            val lon1 = Math.toRadians(fromLongitude)
            val lat2 = Math.toRadians(toLatitude)
            val lon2 = Math.toRadians(toLongitude)

            // Diferencia de longitudes
            val dLon = lon2 - lon1

            // Fórmula del bearing (rumbo inicial en great circle)
            // bearing = atan2(sin(Δlong)⋅cos(lat2), cos(lat1)⋅sin(lat2) − sin(lat1)⋅cos(lat2)⋅cos(Δlong))
            val y = sin(dLon) * cos(lat2)
            val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

            // atan2 devuelve el ángulo en radianes (-π a π)
            val bearingRadians = atan2(y, x)

            // Convertir radianes a grados
            val bearingDegrees = Math.toDegrees(bearingRadians)

            // Normalizar a 0-360
            val normalizedBearing = (bearingDegrees + 360) % 360

            return normalizedBearing.toFloat()
        }
    }
