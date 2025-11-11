package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.Pin
import kotlinx.coroutines.flow.Flow

/**
 * Puerto de Salida en arquitectura Hexagonal.
 */
interface PinRepository {
    /**
     * Obtiene todos los pins guardados.
     */
    suspend fun getAllPins(): Result<List<Pin>>

    /**
     * Guarda un nuevo pin.
     */
    suspend fun savePin(pin: Pin): Result<Unit>

    /**
     * Elimina un pin por su ID.
     */
    suspend fun deletePin(pinId: String): Result<Unit>

    // observa pines en tiempo real
    fun observePins(): Flow<List<Pin>>
}
