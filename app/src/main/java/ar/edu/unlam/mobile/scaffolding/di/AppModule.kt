package ar.edu.unlam.mobile.scaffolding.di

import android.content.Context
import ar.edu.unlam.mobile.scaffolding.data.repository.PetsRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repository.UserRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt.
 * Le enseña a Hilt cómo crear instancias de servicios
 * que no podemos crear con @Inject porque son de librerías externas.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePetsRepository(db: FirebaseFirestore): PetsRepository = PetsRepositoryImpl(db)

    @Module
    @InstallIn(SingletonComponent::class)
    object LocationModule {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context,
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
    ): UserRepository = UserRepositoryImpl(auth, db)
}
