package ar.edu.unlam.mobile.scaffolding.di

import ar.edu.unlam.mobile.scaffolding.data.repository.LocationRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repository.SensorRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.domain.repository.LocationRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.SensorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt que conecta interfaces con sus implementaciones.
 * Le dice a Hilt qué implementación concreta debe usar cuando alguien pide una interface.
 * (Patrón Repository).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /**
     * Conecta LocationRepository (interface) con LocationRepositoryImpl (implementación).
     */
    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    /**
     * Conecta SensorRepository (interface) con SensorRepositoryImpl (implementación).
     */
    @Binds
    @Singleton
    abstract fun bindSensorRepository(impl: SensorRepositoryImpl): SensorRepository
}
