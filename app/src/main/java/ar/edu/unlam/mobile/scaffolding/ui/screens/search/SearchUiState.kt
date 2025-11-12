package ar.edu.unlam.mobile.scaffolding.ui.screens.search

import ar.edu.unlam.mobile.scaffolding.domain.model.DeviceOrientation
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.domain.model.SearchMode
import ar.edu.unlam.mobile.scaffolding.domain.model.UserLocation
import kotlin.math.abs

/**
 * Estado de la UI para la pantalla de búsqueda de mascotas.
 *
 * Contiene toda la información necesaria para renderizar la pantalla:
 * - Ubicación del usuario
 * - Información de la mascota buscada
 * - Modo de búsqueda activo (Ruta o Radar)
 * - Orientación del dispositivo (para el modo Radar)
 * - Estados de carga y errores
 */
data class SearchUiState(
    /**
     * Ubicación actual del usuario.
     * null = aún no se obtuvo la ubicación.
     */
    val userLocation: UserLocation? = null,
    /**
     * Mascota que se está buscando.
     * Contiene la ubicación (latitude/longitude) donde se perdió o fue vista.
     */
    val pet: Pet? = null,
    /**
     * Modo de búsqueda activo.
     * - ROUTE: Muestra polilínea azul por las calles.
     * - RADAR: Muestra flecha roja con orientación directa.
     */
    val searchMode: SearchMode = SearchMode.RADAR,
    /**
     * Orientación del dispositivo (azimut en grados 0-360).
     * Solo se usa en modo RADAR.
     * null = aún no se obtuvo la orientación.
     */
    val deviceOrientation: DeviceOrientation? = null,
    /**
     * Bearing (rumbo) hacia la mascota en grados (0-360).
     * null = aún no se calculó.
     * Se calcula usando las ubicaciones del usuario y la mascota.
     */
    val bearingTowardsPet: Float? = null,
    /**
     * Indica si se está cargando la ubicación del usuario.
     */
    val isLoadingLocation: Boolean = false,
    /**
     * Indica si se tienen permisos de ubicación.
     */
    val hasLocationPermission: Boolean = false,
    /**
     * Mensaje de error (si ocurrió alguno).
     * null = no hay error.
     */
    val errorMessage: String? = null,
) {
    /**
     * Propiedad computada: ¿Está todo listo para mostrar el radar/mapa?
     *
     * Se considera listo cuando:
     * - Tenemos la ubicación del usuario
     * - Tenemos la información de la mascota
     * - Tenemos permisos de ubicación
     */
    val isReady: Boolean
        get() = userLocation != null && pet != null && hasLocationPermission

    /**
     * Propiedad computada: Rotación de la flecha en el modo Radar.
     *
     * La flecha debe apuntar hacia la mascota, considerando hacia dónde
     * está orientado el dispositivo.
     *
     * Fórmula: rotación = bearing - azimut
     *
     * Ejemplo:
     * - Bearing hacia mascota: 90° (este)
     * - Azimut del dispositivo: 45° (noreste)
     * - Rotación de flecha: 90° - 45° = 45°
     *   (la flecha rota 45° desde la dirección del teléfono)
     *
     * @return Ángulo de rotación en grados, o null si no hay datos suficientes.
     */
    val arrowRotation: Float?
        get() {
            // Solo calculamos en modo RADAR
            if (searchMode != SearchMode.RADAR) return null

            // Necesitamos ambos valores
            val bearing = bearingTowardsPet ?: return null
            val azimuth = deviceOrientation?.azimuth ?: return null

            // Calcular la rotación
            var rotation = bearing - azimuth

            // Normalizar a -180 a 180 (para rotaciones más naturales)
            // Ejemplo: en lugar de rotar 270°, es mejor rotar -90°
            if (rotation > 180) {
                rotation -= 360
            } else if (rotation < -180) {
                rotation += 360
            }

            return rotation
        }

    /**
     * Propiedad computada: ¿Está el usuario apuntando correctamente hacia la mascota?
     *
     * Indica si el dispositivo está orientado hacia la dirección de la mascota
     * dentro de un rango de tolerancia (±20 grados).
     *
     * Esta lógica de negocio pertenece al UiState, no a la UI.
     * La UI solo debe leer este valor y renderizar en consecuencia.
     *
     * @return true si está dentro del rango de acierto, false en caso contrario.
     */
    val isPointingCorrectly: Boolean
        get() {
            // Rango de tolerancia para considerar "alineado"
            val coincidenceRangeDegrees = 20f

            // Necesitamos la rotación calculada
            val rotation = arrowRotation ?: return false

            // Está apuntando correctamente si la rotación está cerca de 0°
            return abs(rotation) < coincidenceRangeDegrees
        }
}
