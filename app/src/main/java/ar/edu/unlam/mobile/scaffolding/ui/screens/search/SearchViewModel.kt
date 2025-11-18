package ar.edu.unlam.mobile.scaffolding.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.SearchMode
import ar.edu.unlam.mobile.scaffolding.domain.repository.LocationRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.PetsRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.RouteRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.SensorRepository
import ar.edu.unlam.mobile.scaffolding.domain.usecase.CalculateBearingUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de búsqueda de mascotas.
 *
 * Responsabilidades:
 * - Gestionar el estado de la UI (SearchUiState)
 * - Obtener la ubicación del usuario en tiempo real
 * - Obtener la orientación del dispositivo (modo Radar)
 * - Calcular el bearing hacia la mascota
 * - Cambiar entre modos de búsqueda (Ruta/Radar)
 */
@HiltViewModel
class SearchViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val sensorRepository: SensorRepository,
        private val routeRepository: RouteRepository,
        private val calculateBearingUseCase: CalculateBearingUseCase,
        private val petsRepository: PetsRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        // ===== ESTADO DE LA UI =====

        private val _uiState = MutableStateFlow(SearchUiState())
        val uiState: StateFlow<SearchUiState> = _uiState

        // ===== PET ID DESDE NAVEGACIÓN =====

        private val petId: String = savedStateHandle.get<String>("petId") ?: ""

        // ===== JOBS DE COROUTINES =====

        /**
         * Job que escucha la orientación del dispositivo.
         * Se cancela cuando se sale del modo Radar.
         */
        private var sensorJob: Job? = null

        /**
         * Job que escucha actualizaciones continuas de ubicación.
         * Se cancela cuando se destruye el ViewModel.
         */
        private var locationJob: Job? = null

        /**
         * Job que carga la ruta desde Google Directions API.
         * Se cancela si se inicia una nueva carga o se cambia de modo.
         */
        private var routeJob: Job? = null

        // ===== INICIALIZACIÓN =====

        init {
            loadPet()
            startListeningToLocation()

            // Si el estado inicial es RADAR, arrancar los sensores
            if (_uiState.value.searchMode == SearchMode.RADAR) {
                startListeningToSensors()
            }
        }

        // ===== FUNCIONES PÚBLICAS (LLAMADAS DESDE LA UI) =====

        /**
         * Actualiza el estado del permiso de ubicación.
         *
         * @param granted true si el permiso fue concedido, false si no.
         */
        fun onLocationPermissionChanged(granted: Boolean) {
            _uiState.update { it.copy(hasLocationPermission = granted) }

            // Si se concedió el permiso, iniciar listener de ubicación
            if (granted) {
                startListeningToLocation()
            } else {
                // Si se revocó, detener el listener
                stopListeningToLocation()
            }
        }

        /**
         * Cambia entre los modos de búsqueda (Ruta/Radar).
         *
         * @param mode El nuevo modo de búsqueda.
         */
        fun onSearchModeChanged(mode: SearchMode) {
            _uiState.update { it.copy(searchMode = mode) }

            when (mode) {
                SearchMode.RADAR -> {
                    // Activar el listener de sensores
                    startListeningToSensors()
                    // Cancelar cualquier carga de ruta en progreso
                    routeJob?.cancel()
                }

                SearchMode.ROUTE -> {
                    // Desactivar el listener de sensores
                    stopListeningToSensors()
                    // Cargar la ruta desde Google Directions API
                    loadRoute()
                }
            }
        }

        // ===== FUNCIONES PRIVADAS (LÓGICA INTERNA) =====

        /**
         * Carga la información de la mascota desde Firebase.
         *
         * Usa el petId obtenido de la navegación para cargar los datos reales
         * de la mascota desde PetsRepository.
         *
         * El repositorio ya devuelve Pet del dominio, por lo que no necesita conversión.
         */
        private fun loadPet() {
            petsRepository
                .getPetById(petId)
                .onEach { loadedPet ->
                    // El repository ya devuelve Pet de domain
                    _uiState.update { it.copy(pet = loadedPet) }
                }.catch { exception ->
                    // Manejar errores al cargar el Pet
                    _uiState.update {
                        it.copy(errorMessage = "Error cargando mascota: ${exception.message}")
                    }
                }.launchIn(viewModelScope)
        }

        /**
         * Inicia el listener de ubicación continua.
         * Escucha continuamente cambios en la ubicación del usuario y actualiza el estado.
         */
        private fun startListeningToLocation() {
            // Cancelar el job anterior si existía
            locationJob?.cancel()

            // Indicar que está cargando (solo la primera vez)
            _uiState.update { it.copy(isLoadingLocation = true, errorMessage = null) }

            // Crear un nuevo job que escucha el Flow de ubicación
            locationJob =
                locationRepository
                    .getLocationUpdates()
                    .onEach { userLocation ->
                        // Por cada nueva ubicación, actualizar el estado
                        _uiState.update {
                            it.copy(
                                userLocation = userLocation,
                                isLoadingLocation = false,
                            )
                        }

                        // Recalcular el bearing con la nueva ubicación
                        calculateBearing()
                    }.catch { exception ->
                        // Si el Flow falla (ej. permisos revocados, GPS apagado),
                        // manejar el error aquí
                        _uiState.update {
                            it.copy(
                                isLoadingLocation = false,
                                errorMessage =
                                    exception.message
                                        ?: "Error al obtener ubicación continua",
                            )
                        }
                    }.launchIn(viewModelScope)
        }

        /**
         * Detiene el listener de ubicación.
         * Se llama al salir de la pantalla o si se revoca el permiso.
         */
        private fun stopListeningToLocation() {
            locationJob?.cancel()
            locationJob = null
        }

        /**
         * Calcula el bearing (rumbo) hacia la mascota.
         *
         * Solo se calcula si tenemos ambas ubicaciones (usuario y mascota).
         */
        private fun calculateBearing() {
            val currentState = _uiState.value
            val userLoc = currentState.userLocation ?: return
            val pet = currentState.pet ?: return

            // Llamar al Use Case para calcular el bearing
            val bearing =
                calculateBearingUseCase(
                    fromLatitude = userLoc.latitude,
                    fromLongitude = userLoc.longitude,
                    toLatitude = pet.latitude,
                    toLongitude = pet.longitude,
                )

            // Actualizar el estado con el bearing calculado
            _uiState.update { it.copy(bearingTowardsPet = bearing) }
        }

        /**
         * Inicia el listener de sensores para el modo Radar.
         *
         * Escucha continuamente la orientación del dispositivo y actualiza el estado.
         */
        private fun startListeningToSensors() {
            // Cancelar el job anterior si existía
            sensorJob?.cancel()

            // Crear un nuevo job que escucha el Flow de orientación
            sensorJob =
                sensorRepository
                    .getDeviceOrientation()
                    .onEach { orientation ->
                        // Por cada nuevo valor de orientación, actualizar el estado
                        _uiState.update { it.copy(deviceOrientation = orientation) }
                    }.launchIn(viewModelScope)
        }

        /**
         * Detiene el listener de sensores.
         *
         * Se llama al cambiar al modo Ruta o al salir de la pantalla.
         */
        private fun stopListeningToSensors() {
            sensorJob?.cancel()
            sensorJob = null

            // Limpiar la orientación del estado
            _uiState.update { it.copy(deviceOrientation = null) }
        }

        /**
         * Carga la ruta desde Google Directions API.
         *
         * Solo se ejecuta si tenemos ubicación del usuario y de la mascota.
         * La ruta se guarda en el estado para que la UI la dibuje.
         */
        private fun loadRoute() {
            // Cancelar cualquier carga anterior
            routeJob?.cancel()

            val currentState = _uiState.value
            val userLoc = currentState.userLocation ?: return
            val pet = currentState.pet ?: return

            // Convertir a LatLng de Google Maps
            val origin = LatLng(userLoc.latitude, userLoc.longitude)
            val destination = LatLng(pet.latitude, pet.longitude)

            // Iniciar carga
            _uiState.update { it.copy(isLoadingRoute = true, errorMessage = null) }

            routeJob =
                viewModelScope.launch {
                    val result = routeRepository.getRoute(origin, destination)

                    result.fold(
                        onSuccess = { route ->
                            _uiState.update {
                                it.copy(
                                    route = route,
                                    isLoadingRoute = false,
                                )
                            }
                        },
                        onFailure = { exception ->
                            _uiState.update {
                                it.copy(
                                    isLoadingRoute = false,
                                    errorMessage = "Error al obtener ruta: ${exception.message}",
                                )
                            }
                        },
                    )
                }
        }

        /**
         * Se llama automáticamente cuando el ViewModel se destruye.
         * Limpia los recursos (cancela los listeners).
         */
        override fun onCleared() {
            super.onCleared()
            stopListeningToSensors()
            stopListeningToLocation()
            routeJob?.cancel()
        }
    }
