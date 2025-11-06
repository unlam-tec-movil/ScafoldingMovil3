package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.LocationDataSource
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.domain.model.UserLocation
import ar.edu.unlam.mobile.scaffolding.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Esta clase es el Adaptador de Salida en la arquitectura Hexagonal.
 * Implementa el puerto de salida (LocationRepository del dominio).
 * Se comunica con el DataSource (que trata con Google).
 * Traduce los datos usando el Mapper.
 *
 * Flujo:
 * 1. ViewModel llama a getCurrentLocation()
 * 2. Repository llama a LocationDataSource
 * 3. DataSource obtiene Location de Google
 * 4. Repository traduce Location → UserLocation con el Mapper
 * 5. Repository devuelve Result<UserLocation> al ViewModel
 */
class LocationRepositoryImpl
    @Inject
    constructor(
        private val locationDataSource: LocationDataSource,
    ) : LocationRepository {
        /**
         * Obtiene la ubicación actual del usuario.
         * Maneja Result.success en caso de éxito, o Result.failure en caso de error.
         */
        override suspend fun getCurrentLocation(): Result<UserLocation> =
            try {
                // 1. Obtiene Location de Google vía DataSource
                val location = locationDataSource.getCurrentLocation()

                // 2. Verifica que no sea null
                if (location != null) {
                    // 3. Convierte Location -> UserLocation usando el mapper
                    val userLocation = location.toDomain()

                    // 4. Retorna éxito
                    Result.success(userLocation)
                } else {
                    // Si location es null
                    Result.failure(Exception("No se pudo obtener la ubicación"))
                }
            } catch (e: SecurityException) {
                // Si no hay permisos
                Result.failure(Exception("Permisos de ubicación no concedidos"))
            } catch (e: Exception) {
                // Cualquier otro error
                Result.failure(e)
            }
    }
