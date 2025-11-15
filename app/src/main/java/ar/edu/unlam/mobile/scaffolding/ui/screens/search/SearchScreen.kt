package ar.edu.unlam.mobile.scaffolding.ui.screens.search

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.R
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

const val SEARCH_ROUTE = "search"

/**
 * Pantalla de búsqueda de mascota con modo RADAR.
 *
 * Muestra un mapa con:
 * - Ubicación del usuario (punto azul - automático de Google Maps)
 * - Ubicación de la mascota (marker rojo)
 * - Flecha roja que apunta hacia la mascota (modo RADAR)
 *
 * Nota: El petId se obtiene automáticamente del SavedStateHandle en el ViewModel.
 * No es necesario pasarlo como parámetro a la UI.
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // ===== OBSERVAR ESTADO DEL VIEWMODEL =====
    val uiState by viewModel.uiState.collectAsState()

    // ===== VIBRACIÓN HÁPTICA AL ALINEARSE =====
    // Recordar el Vibrator una sola vez (optimización + compatibilidad moderna)
    val vibrator =
        remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ (API 31+): Usar VibratorManager (forma moderna)
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                // Android < 12: Usar API antigua
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }

    // Recordar el estado anterior para detectar la transición false → true
    var wasPointingCorrectlyBefore by remember { mutableStateOf(false) }

    // Vibrar cuando el usuario se alinea correctamente con la mascota
    LaunchedEffect(uiState.isPointingCorrectly) {
        // Solo vibrar en el momento exacto de alineación (false → true)
        if (uiState.isPointingCorrectly && !wasPointingCorrectlyBefore) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0+ (API 26+)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE),
                )
            } else {
                // Android anterior a 8.0
                @Suppress("DEPRECATION")
                vibrator.vibrate(100) // 100ms - corto y sutil
            }
        }
        wasPointingCorrectlyBefore = uiState.isPointingCorrectly
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
            // Posición inicial: la ubicación de la mascota (o default si no hay)
            val petLocation = uiState.pet?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(0.0, 0.0)
            position = CameraPosition.fromLatLngZoom(petLocation, 15f)
        }

    // ===== ANIMAR CÁMARA CUANDO LLEGA LA UBICACIÓN DEL USUARIO =====
    // En modo RADAR: mantener la cámara centrada en el usuario (HUD)
    LaunchedEffect(uiState.userLocation, uiState.searchMode) {
        if (uiState.searchMode == SearchMode.RADAR) {
            uiState.userLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(latLng),
                    durationMs = 1000,
                )
            }
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
    Box(modifier = modifier.fillMaxSize()) {
        // ===== GOOGLE MAP =====
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties =
                MapProperties(
                    isMyLocationEnabled = false, // Apagado para evitar Z-fighting con el marker
                ),
            uiSettings =
                MapUiSettings(
                    myLocationButtonEnabled = false, // No necesario en modo radar
                    zoomControlsEnabled = true, // Permitir zoom
                    compassEnabled = true,
                    // En modo RADAR: bloquear arrastre (scroll) para mantener usuario centrado
                    scrollGesturesEnabled = (uiState.searchMode != SearchMode.RADAR),
                    zoomGesturesEnabled = true,
                ),
        ) {
            // ===== MARKER DE LA MASCOTA =====
            uiState.pet?.let { pet ->
                val markerState =
                    rememberMarkerState(
                        position = LatLng(pet.latitude, pet.longitude),
                    )

                Marker(
                    state = markerState,
                    title = pet.name,
                    snippet = "Mascota perdida",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                )
            }
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

        // ===== FLECHA ROJA/VERDE FLOTANTE =====
        // La flecha está FIJA en el centro de la pantalla
        // Como la cámara sigue al usuario, el centro de la pantalla = ubicación del usuario
        // La flecha rota según: bearing - azimuth (apunta hacia la mascota relativo a tu orientación)
        // Cambia de color cuando el usuario apunta correctamente (lógica en UiState)
        if (uiState.searchMode == SearchMode.RADAR && uiState.isReady) {
            RadarArrow(
                rotation = uiState.arrowRotation ?: 0f,
                isPointingCorrectly = uiState.isPointingCorrectly,
                modifier = Modifier.align(Alignment.Center),
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
 * Componente de flecha para modo RADAR (HUD).
 * Componente que solo renderiza según el estado recibido.
 *
 * Aparece fija en el centro de la pantalla.
 * Rota para apuntar hacia la mascota relativo a la orientación del dispositivo.
 * Cambia de color según la alineación (calculada por el UiState):
 * - VERDE: Usuario apuntando correctamente hacia la mascota
 * - ROJA: Usuario debe seguir buscando la dirección
 *
 * @param rotation Ángulo de rotación en grados (calculado por UiState)
 * @param isPointingCorrectly True si el usuario está alineado (calculado por UiState)
 * @param modifier Modificador de Compose
 */
@Composable
fun RadarArrow(
    rotation: Float,
    isPointingCorrectly: Boolean,
    modifier: Modifier = Modifier,
) {
    // Elegir la flecha según el estado recibido (sin calcular nada)
    val arrowResource =
        if (isPointingCorrectly) {
            R.drawable.flecha_verde // ¡Perfecto! Camina hacia adelante
        } else {
            R.drawable.flecha_roja // Sigue girando para encontrar la dirección
        }

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Fondo circular semi-transparente
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50),
                    ),
        )

        // Flecha PNG rotada (roja o verde según alineación)
        Image(
            painter = painterResource(arrowResource),
            contentDescription =
                if (isPointingCorrectly) {
                    "¡Alineado! Camina hacia adelante"
                } else {
                    "Gira para encontrar la dirección"
                },
            modifier =
                Modifier
                    .size(60.dp)
                    .rotate(rotation), // Rotación relativa al dispositivo
        )
    }
}
