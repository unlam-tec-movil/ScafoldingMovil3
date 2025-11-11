package ar.edu.unlam.mobile.scaffolding.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.Pin
import ar.edu.unlam.mobile.scaffolding.domain.model.UserLocation
import ar.edu.unlam.mobile.scaffolding.domain.repository.LocationRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MapPostViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val pinRepository: PinRepository,
    ) : ViewModel() {
        // ===== ESTADO DE UBICACIÓN =====

        /**
         * Ubicación actual del usuario.
         * null: aún no se cargó o falló.
         * UserLocation: Coordenadas obtenidas con éxito.
         */
        private val _currentLocation = MutableStateFlow<UserLocation?>(null)
        val currentLocation: StateFlow<UserLocation?> = _currentLocation

        // ===== ESTADO DE PERMISOS =====

        /**
         * Estado del permiso de ubicación.
         * true: usuario concedió el permiso / false: usuario no concedió el permiso.
         */
        private val _hasLocationPermission = MutableStateFlow(false)
        val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission

        // ===== ESTADO DE LOADING =====

        /**
         * Indica si se está cargando la ubicación.
         * true: petición en curso / false: 'Idle' puede ser éxito o error
         */
        private val _isLoadingLocation = MutableStateFlow(false)
        val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation

        // Actualiza el estado del permiso de ubicación.
        fun onLocationPermissionChanged(granted: Boolean) {
            _hasLocationPermission.value = granted
        }

        // Carga la ubicación actual del usuario.
        fun loadCurrentLocation() {
            viewModelScope.launch {
                // 1. Indica que está cargando
                _isLoadingLocation.value = true

                // 2. Llama al repositorio (capa de dominio)
                val result = locationRepository.getCurrentLocation()

                // 3. Maneja el resultado
                result
                    .onSuccess { userLocation ->
                        _currentLocation.value = userLocation
                    }.onFailure { exception ->
                        _currentLocation.value = null

                        // TODO: Emitir evento para mostrar Snackbar con mensaje de error
                        // Por ejemplo: "No se pudo obtener la ubicación."
                    }

                // 4. Indica que terminó la carga
                _isLoadingLocation.value = false
            }
        }

        /**
         * Guarda un nuevo pin.
         *
         * La UI solo pasa los datos primitivos. El ViewModel es responsable
         * de crear el modelo Pin del dominio (incluyendo generar el ID).
         */
        fun savePin(
            latitude: Double,
            longitude: Double,
            title: String,
            description: String?,
        ) {
            viewModelScope.launch {
                // El ViewModel crea el modelo del dominio con su lógica de negocio
                val newPin =
                    Pin(
                        id =
                            UUID
                                .randomUUID()
                                .toString(),
                        // Lógica de generación de ID
                        latitude = latitude,
                        longitude = longitude,
                        imageUrl = TODO(),
                    )
                pinRepository.savePin(newPin)
//                .onSuccess {
                // Recargar la lista de pins para reflejar el cambio
                // loadPins()
//                }.onFailure {
//                    // TODO: Emitir evento de error
//                }
            }
        }

        /**
         * Elimina un pin por su ID.
         */
        fun deletePin(pinId: String) {
            viewModelScope.launch {
                pinRepository
                    .deletePin(pinId)
                    .onSuccess {
                        // Recargar la lista de pins para reflejar el cambio
//                    loadPins()
                    }.onFailure {
                        // TODO: Emitir evento de error
                    }
            }
        }
    }
