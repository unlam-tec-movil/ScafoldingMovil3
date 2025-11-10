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
import ar.edu.unlam.mobile.scaffolding.domain.loadMarkerDescriptorFromUrl
import ar.edu.unlam.mobile.scaffolding.ui.components.AddPinDialog
import ar.edu.unlam.mobile.scaffolding.ui.components.ShowPermissionDenied
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
 * Pantalla del mapa con ubicación del usuario y gestión de pins.
 * Observa estados del MapViewModel (StateFlow).
 * Arquitectura limpia: usa Pin del dominio y PinRepository.
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val markerIcons = remember { mutableStateMapOf<String, BitmapDescriptor>() }

    // ===== OBSERVAR ESTADOS DEL VIEWMODEL =====
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()
    val pins by viewModel.pins.collectAsState()

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

    // ===== GESTIÓN DE PINS =====
    var pendingLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Cargar pins desde el ViewModel al entrar
    LaunchedEffect(Unit) {
        viewModel.loadPins()
    }

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
            onMapLongClick = { latLng ->
                // Abrir diálogo para agregar un nuevo pin
                pendingLatLng = latLng
            },
        ) {
            // Renderizar pins (Observa desde ViewModel, usa modelo Pin del dominio)
            pins.forEach { pin ->

                val position = LatLng(pin.latitude, pin.longitude)
                // si aún no tenemos el ícono de este pet, lo disparamos
                val hasIcon = markerIcons.containsKey(pin.id)

                if (hasIcon) {
                    Marker(
                        state = MarkerState(position),
                        icon = markerIcons[pin.id],
                        anchor = Offset(0.5f, 0.5f),
                        title = "Mascota",
                    )
                }
                // carga asíncrona del icono
                LaunchedEffect(pin.id, pin.imageUrl) {
                    if (!markerIcons.containsKey(pin.id)) {
                        val desc =
                            loadMarkerDescriptorFromUrl(
                                context = context,
                                url = pin.imageUrl,
                            )
                        if (desc != null) {
                            markerIcons[pin.id] = desc
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

    // ========== DIÁLOGO PARA AGREGAR PIN ==========

    if (pendingLatLng != null) {
        AddPinDialog(
            onConfirm = {
                // La UI solo pasa los datos.
                viewModel.savePin(
                    // TODO: modificar esto para que en vés de guardarlo por separado,
                    // se guarde en la publicación del perro
                    latitude = pendingLatLng!!.latitude,
                    longitude = pendingLatLng!!.longitude,
                )
                pendingLatLng = null
            },
            onDismiss = { pendingLatLng = null },
        )
    }
}
