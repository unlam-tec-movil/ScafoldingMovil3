package ar.edu.unlam.mobile.scaffolding.data.mappers

import ar.edu.unlam.mobile.scaffolding.data.models.PlacePin
import ar.edu.unlam.mobile.scaffolding.domain.model.Pin

/**
 * Mapper que convierte objetos entre la capa de datos y el dominio.
 */

/**
 * Convierte un PlacePin (capa de datos) a Pin (dominio).
 *
 * Traduce los nombres de campos:
 * - snippet → description (nombre más claro en el dominio)
 *
 * @return Pin con los datos del PlacePin.
 */
fun PlacePin.toDomain(): Pin {
    return Pin(
        id = this.id,
        latitude = this.lat,
        longitude = this.lng,
        title = this.title,
        description = this.snippet
    )
}

/**
 * Convierte un Pin (dominio) a PlacePin (capa de datos).
 *
 * Traduce los nombres de campos:
 * - description → snippet (como espera DataStore)
 *
 * @return PlacePin listo para ser guardado en DataStore.
 */
fun Pin.toData(): PlacePin {
    return PlacePin(
        id = this.id,
        lat = this.latitude,
        lng = this.longitude,
        title = this.title,
        snippet = this.description
    )
}
