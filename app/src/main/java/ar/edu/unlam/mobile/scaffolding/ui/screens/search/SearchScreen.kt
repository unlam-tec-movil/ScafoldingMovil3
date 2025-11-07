package ar.edu.unlam.mobile.scaffolding.ui.screens.search

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.domain.model.SearchMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

const val SEARCH_ROUTE = "search"

/**
 * Pantalla de búsqueda de mascota con modo RADAR.
 *
 * Muestra un mapa con:
 * - Ubicación del usuario (punto azul - automático de Google Maps)
 * - Ubicación de la mascota (marker rojo)
 * - Flecha roja que apunta hacia la mascota (modo RADAR)
 *
 * @param petId ID de la mascota que se está buscando.
 * @param viewModel El ViewModel que gestiona el estado.
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    petId: String,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // ===== CREAR MASCOTA TEMPORAL (HARDCODED) =====
    // TODO: Cuando tengas el PetRepository, cargar la mascota real desde la BD
    val pet =
        remember(petId) {
            Pet(
                id = petId,
                name = "Luna",
                type = "Perro",
                status = "Perdida",
                gender = "Hembra",
                seenAt = "2025-01-15",
                locality = "Palermo, Buenos Aires",
                latitude = -34.5875,
                longitude = -58.4197,
                imageUrl = "",
            )
        }

    // ===== OBSERVAR ESTADO DEL VIEWMODEL =====
    val uiState by viewModel.uiState.collectAsState()

    // ===== INICIALIZAR BÚSQUEDA =====
    LaunchedEffect(pet) {
        viewModel.initSearch(pet)
    }

    // ===== MANEJO DE PERMISOS CON ACCOMPANIST =====
    val locationPermissionState =
        rememberPermissionState(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )

    // Estado para detectar si es la primera carga
    var isFirstLoad by remember { mutableStateOf(true) }

    // ===== PEDIR PERMISO AUTOMÁTICAMENTE AL ENTRAR =====
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
            isFirstLoad = false
        }
    }

    // ===== NOTIFICAR AL VIEWMODEL SOBRE CAMBIOS DE PERMISO =====
    LaunchedEffect(locationPermissionState.status.isGranted) {
        viewModel.onLocationPermissionChanged(locationPermissionState.status.isGranted)
    }

    // ===== CONFIGURACIÓN DE LA CÁMARA =====
    val cameraPositionState =
        rememberCameraPositionState {
            // Posición inicial: la ubicación de la mascota
            position =
                CameraPosition.fromLatLngZoom(
                    LatLng(pet.latitude, pet.longitude),
                    15f,
                )
        }

    // ===== ANIMAR CÁMARA CUANDO LLEGA LA UBICACIÓN DEL USUARIO =====
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                durationMs = 800,
            )
        }
    }

    // ===== UI: MANEJAR ESTADO DEL PERMISO =====
    when (val status = locationPermissionState.status) {
        is PermissionStatus.Denied -> {
            // Si es la primera carga, mostrar loading
            if (isFirstLoad) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                return
            }

            // Si usuario rechazó, mostrar mensaje explicativo
            val isBlocked = !status.shouldShowRationale

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text =
                            if (isBlocked) {
                                "Permiso de ubicación bloqueado.\nVe a Configuración para habilitarlo."
                            } else {
                                "Esta app necesita tu ubicación\npara guiarte hacia la mascota."
                            },
                        textAlign = TextAlign.Center,
                    )

                    Button(
                        onClick = {
                            if (isBlocked) {
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                context.startActivity(intent)
                            } else {
                                locationPermissionState.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(if (isBlocked) "Abrir Configuración" else "Conceder permiso")
                    }
                }
            }
            return
        }
        is PermissionStatus.Granted -> {
            // Permiso concedido, continuar con el mapa
        }
    }

    // ===== UI: MAPA CON RADAR =====
    Box(modifier = Modifier.fillMaxSize()) {
        // ===== GOOGLE MAP =====
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties =
                MapProperties(
                    isMyLocationEnabled = true, // Punto azul del usuario + haz de luz
                ),
            uiSettings =
                MapUiSettings(
                    myLocationButtonEnabled = true, // Botón para centrar
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                ),
        ) {
            // ===== MARKER DE LA MASCOTA =====
            Marker(
                state = MarkerState(LatLng(pet.latitude, pet.longitude)),
                title = pet.name,
                snippet = "Mascota perdida",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
            )
        }

        // ===== SEGMENTED BUTTON PARA CAMBIAR MODO =====
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier =
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(50),
                        ).padding(4.dp),
            ) {
                SegmentedButton(
                    selected = uiState.searchMode == SearchMode.ROUTE,
                    onClick = { /* viewModel.onSearchModeChanged(SearchMode.ROUTE) */ },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    enabled = false, // Deshabilitado hasta implementar ROUTE
                ) {
                    Text("Ruta")
                }
                SegmentedButton(
                    selected = uiState.searchMode == SearchMode.RADAR,
                    onClick = { viewModel.onSearchModeChanged(SearchMode.RADAR) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                ) {
                    Text("Radar")
                }
            }

            // Mensaje explicativo si ROUTE está deshabilitado
            if (uiState.searchMode == SearchMode.ROUTE) {
                Text(
                    text = "Modo Ruta próximamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        // ===== FLECHA ROJA (MODO RADAR) =====
        if (uiState.searchMode == SearchMode.RADAR && uiState.isReady) {
            RadarArrow(
                rotation = uiState.arrowRotation ?: 0f,
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(100.dp),
            )
        }

        // ===== INDICADOR DE CARGA =====
        if (uiState.isLoadingLocation) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
            )
        }

        // ===== MENSAJE DE ERROR =====
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                        ).padding(12.dp),
            )
        }
    }
}

/**
 * Componente que dibuja una flecha roja rotada.
 *
 * La flecha apunta hacia la mascota considerando la orientación del dispositivo.
 *
 * @param rotation Ángulo de rotación en grados.
 * @param modifier Modificador de Compose.
 */
@Composable
fun RadarArrow(
    rotation: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Fondo semi-transparente
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50),
                    ),
        )

        // Flecha rotada
        Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = "Dirección hacia la mascota",
            tint = Color.Red,
            modifier =
                Modifier
                    .size(60.dp)
                    .rotate(rotation), // Rotar según el cálculo del ViewModel
        )
    }
}
