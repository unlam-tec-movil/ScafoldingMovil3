package ar.edu.unlam.mobile.scaffolding.data.repository

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.PinRemoteDataSource
import ar.edu.unlam.mobile.scaffolding.data.dto.PlacePin
import ar.edu.unlam.mobile.scaffolding.data.mappers.toData
import ar.edu.unlam.mobile.scaffolding.data.mappers.toDomain
import ar.edu.unlam.mobile.scaffolding.domain.model.Pin
import ar.edu.unlam.mobile.scaffolding.domain.repository.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio de Pins.
 */
class PinRepositoryImpl
@Inject
constructor(
    private val pinRemoteDataSource: PinRemoteDataSource,
) : PinRepository {
    /**
     * Obtiene todos los pins guardados.
     */
    override suspend fun getAllPins(): Result<List<Pin>> =
        try {
            val placePins = pinRemoteDataSource.getAllPins()
            val domainPins = placePins.map { it.toDomain() }
            Result.success(domainPins)
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Guarda un nuevo pin.
     */
    override suspend fun savePin(pin: Pin): Result<Unit> =
        try {
            val currentPins = pinRemoteDataSource.getAllPins().toMutableList()
            val newPlacePin = pin.toData()
            currentPins.add(newPlacePin)

            val success = pinRemoteDataSource.savePins(currentPins)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo guardar el pin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Elimina un pin por su ID.
     */
    override suspend fun deletePin(pinId: String): Result<Unit> =
        try {
            val currentPins = pinRemoteDataSource.getAllPins()
            val updatedPins = currentPins.filter { it.id != pinId }

            val success = pinRemoteDataSource.savePins(updatedPins)
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar el pin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    // Devuelve la lista de pines en tiempo real
    override fun observePins(): Flow<List<Pin>> =
        pinRemoteDataSource.observePins().map { placePinList: List<PlacePin> ->
            placePinList.map { it.toDomain() }
        }
}
