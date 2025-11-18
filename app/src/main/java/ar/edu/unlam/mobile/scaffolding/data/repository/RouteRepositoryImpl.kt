package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.BuildConfig
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.data.remote.GoogleDirectionsApi
import ar.edu.unlam.mobile.scaffolding.domain.model.Route
import ar.edu.unlam.mobile.scaffolding.domain.repository.RouteRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

/**
 * Implementación del repositorio de rutas usando Google Directions API.
 *
 * - Convertir LatLng a formato String que acepta la API
 * - Llamar a Google Directions API usando Retrofit
 * - Manejar errores de red y API
 * - Mapear la respuesta (DTO) a modelo de dominio (Route)
 * - Envolver el resultado en Result<Route> para manejo de errores explícito
 */
class RouteRepositoryImpl
    @Inject
    constructor(
        private val api: GoogleDirectionsApi
    ) : RouteRepository {
    /**
     * @param origin Punto de inicio (ubicación del usuario)
     * @param destination Punto final (ubicación de la mascota)
     * @return Result<Route> (con éxito contiene la ruta calculada, con error contiene la excepción)
     */
    override suspend fun getRoute(
        origin: LatLng,
        destination: LatLng
    ): Result<Route> {
        return try {
            // 1. Convertir LatLng a String en formato "latitud,longitud"
            // Google Directions API espera: "?origin=-346693,-58.5327"
            val originString = "${origin.latitude},${origin.longitude}"
            val destinationString = "${destination.latitude},${destination.longitude}"

            // 2. Obtener API key desde BuildConfig (configurada en local.properties)
            val apiKey = BuildConfig.GOOGLE_DIRECTIONS_API_KEY

            val response =
                api.getRoute(
                    origin = originString,
                    destination = destinationString,
                    apiKey = apiKey
                    // mode y lenguage usan valores por defecto
                )

            // 3. Validar que la respuesta sea exitosa
            // Google devuelve status="OK" si encontró ruta
            if (response.status != "OK") {
                // Si el status no es OK, hay un problema
                return Result.failure(
                    IllegalStateException("Google Directions API error: ${response.status}")
                )
            }

            // 4. Mapear el DTO a modelo de Domain
            // El mapper puede lanzar excepciones si la respuesta está mal formateada
            val route = response.toDomain()

            // 5. Retornar éxito con la ruta
            Result.success(route)
        } catch (e: retrofit2.HttpException) {
            // Error HTTP
            Result.failure(
                Exception("Error HTTP al obtener ruta: ${e.code()} - ${e.message()}", e)
            )
        } catch (e: java.io.IOException) {
            // Error de red (sin internet, timeout)
            Result.failure(
                Exception("Error de red al obtener ruta: ${e.message}", e)
            )
        } catch (e: IllegalStateException) {
            // Error del mapper (respuesta mal formateada)
            Result.failure(
                Exception("Error procesando la ruta: ${e.message}", e)
            )
        } catch (e: Exception) {
            // Cualquier otro error
            Result.failure(
                Exception("Error inesperado al obtener la ruta: ${e.message}", e)
            )
        }
    }
}
