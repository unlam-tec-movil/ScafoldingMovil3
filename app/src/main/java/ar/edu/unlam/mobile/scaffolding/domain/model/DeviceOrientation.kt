package ar.edu.unlam.mobile.scaffolding.domain.model

/**
 * Este modelo representa la orientación del dispositivo.
 * Utiliza sensores (magnetómetro + acelerómetro)
 * para determinar hacia dónde apunta el móvil.
 *
 * La propiedad azimuth es el ángulo en grados medido desde el norte en sentido horario.
 * - 0° = Norte
 * - 90° = Este
 * - 180° = Sur
 * - 270° = Oeste
 */
data class DeviceOrientation (
    val azimuth: Float,
)
