package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.Route
import com.google.android.gms.maps.model.LatLng

/**
 * Calcula una ruta entre dos puntos.
 *
 * @param origin Punto de inicio de la ruta (ubicación del usuario).
 * @param destination Punto final de la ruta (ubicación de la mascota).
 * @return Result<Route>
 *     - Success: Contiene la ruta calculada.
 *     - Failure: Contiene la excepción si hubo error.
 */
interface RouteRepository {
    suspend fun getRoute(
        origin: LatLng,
        destination: LatLng,
    ): Result<Route>
}
