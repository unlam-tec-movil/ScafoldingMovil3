package ar.edu.unlam.mobile.scaffolding.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.domain.model.SearchMode
import ar.edu.unlam.mobile.scaffolding.domain.repository.LocationRepository
import ar.edu.unlam.mobile.scaffolding.domain.repository.SensorRepository
import ar.edu.unlam.mobile.scaffolding.domain.usecase.CalculateBearingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val calculateBearingUseCase: CalculateBearingUseCase,
) : ViewModel() {
    // ===== ESTADO DE LA UI =====

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    // ===== JOBS DE COROUTINES =====

    /**
     * Job que escucha la orientación del dispositivo.
     * Se cancela cuando se sale del modo Radar.
     */
    private var sensorJob: Job? = null

    // ===== FUNCIONES PÚBLICAS (LLAMADAS DESDE LA UI) =====

    /**
     * Inicializa la búsqueda con una mascota específica.
     *
     * @param pet La mascota que se está buscando.
     */
    fun initSearch(pet: Pet) {
        _uiState.update { it.copy(pet = pet) }

        // Cargar la ubicación del usuario
        loadUserLocation()

        // Calcular el bearing hacia la mascota
        calculateBearing()
    }

    /**
     * Actualiza el estado del permiso de ubicación.
     *
     * @param granted true si el permiso fue concedido, false si no.
     */
    fun onLocationPermissionChanged(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }

        // Si se concedió el permiso, cargar la ubicación
        if (granted) {
            loadUserLocation()
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
            }
            SearchMode.ROUTE -> {
                // Desactivar el listener de sensores
                stopListeningToSensors()
            }
        }
    }

    /**
     * Recarga la ubicación del usuario manualmente.
     * Útil si el usuario presiona un botón de "actualizar ubicación".
     */
    fun refreshLocation() {
        loadUserLocation()
    }

    // ===== FUNCIONES PRIVADAS (LÓGICA INTERNA) =====

    /**
     * Carga la ubicación actual del usuario.
     */
    private fun loadUserLocation() {
        viewModelScope.launch {
            // Indicar que está cargando
            _uiState.update { it.copy(isLoadingLocation = true, errorMessage = null) }

            // Obtener la ubicación del repositorio
            val result = locationRepository.getCurrentLocation()

            result
                .onSuccess { userLocation ->
                    // Actualizar el estado con la ubicación
                    _uiState.update {
                        it.copy(
                            userLocation = userLocation,
                            isLoadingLocation = false,
                        )
                    }

                    // Recalcular el bearing con la nueva ubicación
                    calculateBearing()
                }.onFailure { exception ->
                    // Actualizar el estado con el error
                    _uiState.update {
                        it.copy(
                            isLoadingLocation = false,
                            errorMessage = exception.message ?: "Error al obtener ubicación",
                        )
                    }
                }
        }
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
     * Se llama automáticamente cuando el ViewModel se destruye.
     * Limpia los recursos (cancela el listener de sensores).
     */
    override fun onCleared() {
        super.onCleared()
        stopListeningToSensors()
    }
}
