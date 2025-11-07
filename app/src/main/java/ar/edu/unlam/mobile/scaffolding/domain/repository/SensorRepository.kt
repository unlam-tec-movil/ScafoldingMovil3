package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.DeviceOrientation
import kotlinx.coroutines.flow.Flow

/**
 * Puerto de Salida.
 * Define el contrato para obtener la orientación del dispositivo.
 */
interface SensorRepository {
    fun getDeviceOrientation(): Flow<DeviceOrientation>
}
