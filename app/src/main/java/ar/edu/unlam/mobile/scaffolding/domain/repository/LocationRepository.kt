package ar.edu.unlam.mobile.scaffolding.domain.repository

import ar.edu.unlam.mobile.scaffolding.domain.model.UserLocation

/**
 * Puerto de salida.
 * Define el contrato para obtener la ubicación del usuario.
 * El Dominio define qué necesita, y la capa de Datos define cómo se obtiene.
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): Result<UserLocation>
}
