package ar.edu.unlam.mobile.scaffolding.data.remote

import ar.edu.unlam.mobile.scaffolding.data.dto.DirectionsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para Google Directions API.
 * Define los endpoints para obtener rutas entre dos puntos.
 */

interface GoogleDirectionsApi {
    @GET("directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "walking",
        @Query("language") language: String = "es",
        @Query("key") apiKey: String
    ): DirectionsResponseDto
}
