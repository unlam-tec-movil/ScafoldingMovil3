package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Esta clase habla directamente con la API de Google (FusedLocationProviderClient).
 * Obtiene la última ubicación conocida del usuario.
 */
class LocationDataSource
    @Inject
    constructor(
        private val fusedLocationClient: FusedLocationProviderClient,
    ) {
        @SuppressLint("MissingPermission")
        suspend fun getCurrentLocation(): Location? =
            try {
                // Crea un token de cancelación para la petición
                val cancellationTokenSource = CancellationTokenSource()

                // Solicita ubicación
                fusedLocationClient
                    .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token,
                    ).await()
            } catch (e: Exception) {
                // Si algo falla retorna null
                null
            }

        /**
         * Obtiene actualizaciones continuas de ubicación.
         * Usa callbackFlow para convertir los callbacks de Google en un Flow reactivo.
         */
        @SuppressLint("MissingPermission")
        fun getLocationUpdates(): Flow<Location> =
            callbackFlow {
                // Configurar petición de ubicación
                val locationRequest =
                    LocationRequest
                        .Builder(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            5000L,
                        ) // Actualización cada 5 segundos
                        .setMinUpdateIntervalMillis(2000L) // Mínimo 2 segundos entre actualizaciones
                        .setWaitForAccurateLocation(false)
                        .build()

                // Callback que escucha nuevas ubicaciones
                val locationCallback =
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            // Por cada ubicación recibida, emitirla al Flow
                            result.lastLocation?.let { location ->
                                trySend(location)
                            }
                        }
                    }

                // Registrar el listener de ubicaciones
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper(),
                )

                // Cuando el Flow se cancela, remover el listener
                awaitClose {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
    }
