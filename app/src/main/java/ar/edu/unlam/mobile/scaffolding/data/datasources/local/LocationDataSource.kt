package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
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
    }
