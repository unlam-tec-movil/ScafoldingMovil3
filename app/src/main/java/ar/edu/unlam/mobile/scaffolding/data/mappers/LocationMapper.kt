package ar.edu.unlam.mobile.scaffolding.data.mappers

import android.location.Location
import ar.edu.unlam.mobile.scaffolding.domain.model.UserLocation

/**
 * Este mapper traduce Location (de Google Play Services)
 * a UserLocation (que es el modelo de dominio de la app).
 */
fun Location.toDomain(): UserLocation =
    UserLocation(
        latitude = this.latitude,
        longitude = this.longitude,
    )
