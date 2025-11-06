package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.PinLocalDataSource
import ar.edu.unlam.mobile.scaffolding.data.mappers.toData
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.domain.model.Pin
import ar.edu.unlam.mobile.scaffolding.domain.repository.PinRepository
import javax.inject.Inject

/**
 * Implementación del repositorio de Pins.
 */
class PinRepositoryImpl @Inject constructor(
    private val pinLocalDataSource: PinLocalDataSource
) : PinRepository {

    /**
     * Obtiene todos los pins guardados.
     */
    override suspend fun getAllPins(): Result<List<Pin>> {
        return try {
            val placePins = pinLocalDataSource.getAllPins()
            val domainPins = placePins.map { it.toDomain() }
            Result.success(domainPins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Guarda un nuevo pin.
     */
    override suspend fun savePin(pin: Pin): Result<Unit> {
        return try {
            val currentPins = pinLocalDataSource.getAllPins().toMutableList()
            val newPlacePin = pin.toData()
            currentPins.add(newPlacePin)

            val success = pinLocalDataSource.savePins(currentPins)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo guardar el pin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un pin por su ID.
     */
    override suspend fun deletePin(pinId: String): Result<Unit> {
        return try {
            val currentPins = pinLocalDataSource.getAllPins()
            val updatedPins = currentPins.filter { it.id != pinId }

            val success = pinLocalDataSource.savePins(updatedPins)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar el pin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
