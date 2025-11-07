package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SensorDataSource
import ar.edu.unlam.mobile.scaffolding.domain.model.DeviceOrientation
import ar.edu.unlam.mobile.scaffolding.domain.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de sensores.
 *
 * Este es el Adaptador de Salida en la arquitectura Hexagonal.
 * Implementa el puerto de salida (SensorRepository del dominio).
 * Se comunica con el SensorDataSource (que habla con SensorManager de Android).
 *
 * Flujo:
 * 1. ViewModel llama a getDeviceOrientation()
 * 2. Repository llama a SensorDataSource.getOrientation()
 * 3. DataSource obtiene azimut de los sensores (magnetómetro + acelerómetro)
 * 4. Repository traduce Float → DeviceOrientation
 * 5. Repository devuelve Flow<DeviceOrientation> al ViewModel
 */
class SensorRepositoryImpl
@Inject
constructor(
    private val sensorDataSource: SensorDataSource,
) : SensorRepository {
    /**
     * Obtiene un stream continuo de la orientación del dispositivo.
     *
     * Convierte el Flow<Float> del DataSource en Flow<DeviceOrientation> del dominio.
     */
    override fun getDeviceOrientation(): Flow<DeviceOrientation> =
        sensorDataSource
            .getOrientation()
            .map { azimuth ->
                // Convierte Float (capa de datos) → DeviceOrientation (capa de dominio)
                DeviceOrientation(azimuth = azimuth)
            }
}
