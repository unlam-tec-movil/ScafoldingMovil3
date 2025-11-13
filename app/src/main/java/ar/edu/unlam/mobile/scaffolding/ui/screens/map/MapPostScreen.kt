package ar.edu.unlam.mobile.scaffolding.ui.screens.map

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.data.dto.TipoDePublicacion
import ar.edu.unlam.mobile.scaffolding.ui.components.AddPinDialog
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostViewModel
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo
import ar.edu.unlam.mobile.scaffolding.ui.theme.PetFinderFont
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

// PANTALLA QUE APARECE AL TOCAR EL BOTÓN DE SUBIR UN POST, ES PARA PONER LA
// UBICACIÓN DE LA MASCOTA PERDIDA/ENCONTRADA.
// NO MUESTRA PINS, SOLO PERMITE PONER UNO.

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPostScreen(
    viewModel: MapPostViewModel = hiltViewModel(),
    navController: NavController,
    postViewModel: PostViewModel,
) {
    val context = LocalContext.current
    val tipoDePublicacion by postViewModel.postTipo.collectAsState()
    // Estados del ViewModel
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()

    // Permiso de ubicación
    val locationPermissionState =
        rememberPermissionState(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )

    var isFirstLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
            isFirstLoad = false
        }
    }

    // ======= PINS =======
    var pendingLatLng by remember { mutableStateOf<LatLng?>(null) }
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }

    // ======= Ubicación actual =======
    LaunchedEffect(locationPermissionState.status.isGranted) {
        viewModel.onLocationPermissionChanged(locationPermissionState.status.isGranted)
        if (locationPermissionState.status.isGranted) {
            viewModel.loadCurrentLocation()
        }
    }

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(-34.603722, -58.381592), 13f)
        }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                durationMs = 800,
            )
        }
    }

    // ======= PERMISO DENEGADO =======
    when (val status = locationPermissionState.status) {
        is PermissionStatus.Denied -> {
            if (isFirstLoad) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                return
            }

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
                                "Esta app necesita tu ubicación\npara mostrarte mascotas perdidas cerca de ti."
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

        is PermissionStatus.Granted -> Unit
    }

    // ======= UI: MAPA Y TEXTO SUPERIOR =======
    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings =
                MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                ),
            onMapLongClick = { latLng ->
                pendingLatLng = latLng
                title = ""
                snippet = ""
            },
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            CirculoDecorativoMapa(
                modifier =
                    Modifier
                        .padding(top = 16.dp)
                        .offset(y = (-400).dp),
            )

            Text(
                text =
                    "Mantén presionado" +
                        " para añadir la ubicación de " +
                        "dónde lo viste por última vez ",
                fontFamily = PetFinderFont,
                color = Color.White,
                fontSize = 22.sp,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(15.dp),
                textAlign = TextAlign.Center,
            )

            // Loading
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
    Log.d("DEBUG", "VM MapPostScreen: ${postViewModel.hashCode()}")

    // ======= DIÁLOGO PARA AGREGAR PIN =======
    if (pendingLatLng != null) {
        AddPinDialog(
            onConfirm = {
                viewModel.savePin(
                    latitude = pendingLatLng!!.latitude,
                    longitude = pendingLatLng!!.longitude,
                    title = title.ifBlank { "Marcador" },
                    description = snippet.ifBlank { null },
                )
                pendingLatLng = null
                Toast.makeText(context, "Pin guardado correctamente", Toast.LENGTH_SHORT).show()
                when (tipoDePublicacion) {
                    TipoDePublicacion.MASCOTAPERDIDA -> navController.navigate("post_missing_pet_screen")
                    TipoDePublicacion.MASCOTAENCONTRADA -> navController.navigate("post_found_pet_screen")

                    else -> {
                        Toast
                            .makeText(context, "Error: postMode es null", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            onDismiss = { pendingLatLng = null },
        )
    }
}

@Composable
fun CirculoDecorativoMapa(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .width(1000.dp)
                .height(500.dp)
                .graphicsLayer {
                    scaleX = 1.5f
                }
                .background(
                    color = ColorTwo,
                    shape = RoundedCornerShape(180.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
    }
}
