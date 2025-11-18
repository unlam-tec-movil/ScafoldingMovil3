package ar.edu.unlam.mobile.scaffolding.di

import ar.edu.unlam.mobile.scaffolding.BuildConfig
import ar.edu.unlam.mobile.scaffolding.data.remote.GoogleDirectionsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo de Hilt para configurar las dependencias de red.
 *
 * Provee:
 * - OkHttpClient: Cliente HTTP con logging y timeouts
 * - Retrofit: Cliente para llamadas REST
 * - GoogleDirectionsApi: Interface para Google Directions API
 *
 * Todas las dependencias son Singleton porque queremos reutilizar
 * las mismas instancias en toda la app (eficiencia y consistencia).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Provee el cliente OkHttp configurado.
     *
     * - Logging: Solo en DEBUG para ver peticiones en Logcat
     * - Timeouts: 30 segundos para conexión, lectura y escritura
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
            }

        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provee la instancia de Retrofit configurada.
     *
     * - Base URL: API de Google Maps
     * - Converter: Gson para JSON
     * - Client: OkHttpClient configurado
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    /**
     * Provee la interface de Google Directions API.
     *
     * Retrofit crea la implementación automáticamente basándose
     * en las anotaciones de la interface.
     */
    @Provides
    @Singleton
    fun provideGoogleDirectionsApi(retrofit: Retrofit): GoogleDirectionsApi =
        retrofit.create(GoogleDirectionsApi::class.java)
}
