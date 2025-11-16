package ar.edu.unlam.mobile.scaffolding.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.ui.components.ShowPermissionDenied
import ar.edu.unlam.mobile.scaffolding.ui.utils.map.loadMarkerDescriptorFromUrl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

const val MAP_ROUTE = "map"

/**
 * Pantalla del mapa principal con todas las mascotas.
 *
 * Responsabilidad:
 * - Mostrar la ubicación del usuario
 * - Mostrar todas las mascotas (Pet) en el mapa como markers
 * - Permitir interacción con los markers para ver detalles
 *
 * Arquitectura limpia: usa Pet del dominio y PetsRepository.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val markerIcons = remember { mutableStateMapOf<String, BitmapDescriptor>() }

    // ===== OBSERVAR ESTADOS DEL VIEWMODEL =====
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()
    val pets by viewModel.pets.collectAsState()

    // ===== MANEJO DE PERMISOS CON ACCOMPANIST =====
    val locationPermissionState =
        rememberPermissionState(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )

    // Estado para detectar si es la primera carga (evita mostrar mensaje antes del diálogo)
    var isFirstLoad by remember { mutableStateOf(true) }

    // ===== PEDIR PERMISO AUTOMÁTICAMENTE AL ENTRAR =====
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
            isFirstLoad = false // Ya se pidió el permiso
        }
    }

    // ===== GESTIÓN DE MASCOTAS =====
    // Las mascotas se cargan automáticamente en el init{} del ViewModel
    // mediante petsRepository.getAllPets() (Flow reactivo)

    // ========== NOTIFICAR AL VIEWMODEL SOBRE CAMBIOS DE PERMISO ==========
    LaunchedEffect(locationPermissionState.status.isGranted) {
        viewModel.onLocationPermissionChanged(locationPermissionState.status.isGranted)

        // Si se concede el permiso, cargar ubicación
        if (locationPermissionState.status.isGranted) {
            viewModel.loadCurrentLocation()
        }
    }

    // ========== CONFIGURACIÓN DE LA CÁMARA ==========
    val cameraPositionState =
        rememberCameraPositionState {
            // Posición inicial: Buenos Aires (si no hay ubicación)
            position = CameraPosition.fromLatLngZoom(LatLng(-34.603722, -58.381592), 13f)
        }

    // ========== ANIMAR CÁMARA CUANDO LLEGA LA UBICACIÓN ==========
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                durationMs = 800,
            )
        }
    }

    // ========== UI: MANEJAR ESTADO DEL PERMISO ==========
    when (val status = locationPermissionState.status) {
        is PermissionStatus.Denied -> {
            // Si es la primera carga, mostrar loading (no el mensaje)
            // Esto evita el "flash" del mensaje antes de que aparezca el diálogo del sistema

            ShowPermissionDenied(isFirstLoad, status, context, locationPermissionState)
            return
        }

        is PermissionStatus.Granted -> {
            // Permiso concedido, continuar con el mapa (código abajo)
        }
    }

    // ========== UI: MAPA CON LOADING ==========
    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa de Google
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties =
                MapProperties(
                    isMyLocationEnabled = true, // Punto azul del usuario
                ),
            uiSettings =
                MapUiSettings(
                    myLocationButtonEnabled = true, // Botón para centrar
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                ),
        ) {
            // Renderizar mascotas (Observa desde ViewModel, usa modelo Pet del dominio)
            pets.forEach { pet ->
                val position = LatLng(pet.latitude, pet.longitude)

                // Verificar si ya tenemos el ícono de esta mascota
                val hasIcon = markerIcons.containsKey(pet.id)

                if (hasIcon) {
                    Marker(
                        state = MarkerState(position),
                        icon = markerIcons[pet.id],
                        anchor = Offset(0.5f, 0.5f),
                        title = pet.name.ifBlank { "Mascota" },
                        snippet = "${pet.type} - ${pet.status}",
                    )
                }

                // Carga asíncrona del ícono desde la URL
                LaunchedEffect(pet.id, pet.imageUrl) {
                    if (!markerIcons.containsKey(pet.id)) {
                        val desc =
                            loadMarkerDescriptorFromUrl(
                                context = context,
                                url = pet.imageUrl,
                            )
                        if (desc != null) {
                            markerIcons[pet.id] = desc
                        }
                    }
                }
            }
        }

        // Indicador de carga (observa ViewModel)
        if (isLoadingLocation) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
            )
        }
    }
}
