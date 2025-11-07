package ar.edu.unlam.mobile.scaffolding.di

import android.content.Context
import android.hardware.SensorManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt.
 * Le enseña a Hilt cómo crear instancias de servicios
 * que no podemos crear con @Inject porque son de librerías externas
 * o servicios del sistema Android.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provee el cliente de ubicación de Google Play Services.
     * Se usa para obtener la ubicación GPS del usuario.
     */
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context,
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Provee el SensorManager de Android.
     * Se usa para acceder a los sensores del dispositivo (magnetómetro, acelerómetro).
     */
    @Provides
    @Singleton
    fun provideSensorManager(
        @ApplicationContext context: Context,
    ): SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
}
