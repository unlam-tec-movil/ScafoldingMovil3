package ar.edu.unlam.mobile.scaffolding.ui.screens.map

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.PinStorage
import ar.edu.unlam.mobile.scaffolding.data.models.PlacePin
import ar.edu.unlam.mobile.scaffolding.ui.components.AddPinDialog
import ar.edu.unlam.mobile.scaffolding.util.awaitCatching
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

const val MAP_ROUTE = "map"

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MapScreen(viewmodel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pins by viewmodel.pins.collectAsState()
    val pending by viewmodel.pendingLatLng.collectAsState()

    LaunchedEffect(Unit) {
        val saved = PinStorage.load(context)
        viewmodel.setAll(saved)
    }

    LaunchedEffect(pins) {
        PinStorage.save(context, pins)
    }

    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    if (!hasLocationPermission) {
        RequestLocationPermission()
    }

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(1.0, -58.791), 13f) // Moreno pa
        }

    var deleteCandidate by remember { mutableStateOf<PlacePin?>(null) }
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fused = LocationServices.getFusedLocationProviderClient(context)

            val loc: Location? = fused.lastLocation.awaitCatching()
            loc?.let {
                val here = LatLng(it.latitude, it.longitude)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(here, 15f),
                    durationMs = 800,
                )
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties =
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
            ),
        onMapLongClick = { latLng ->
            viewmodel.askAddAt(latLng)
            title = ""
            snippet = ""
        },
        uiSettings =
            MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                compassEnabled = true,
            ),
    ) {
        MarkerInfoWindowContent(
            state = rememberMarkerState(position = LatLng(-34.653, -58.791)),
            title = "perros",
            onClick = { false },
        ) { marker ->
            Card(
                modifier = Modifier.width(220.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                Column(Modifier.padding(12.dp)) {
                    coil.compose.AsyncImage(
                        model =
                            "https://www.google.com/url?sa=i&url=https%3A%2F%2Fben10.fandom.com" +
                                "%2Fes%2Fwiki%2FHighbreed&psig=AOvVaw3QIPq5y6onrTDqxijv5JsC&ust=1761415939421000" +
                                "&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCOjrwc_AvZADFQAAAAAdAAAAABAE",
                        contentDescription = null,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(marker.title ?: "Sin título", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        pins.forEach { pin ->
            Marker(
                state = MarkerState(LatLng(pin.lat, pin.lng)),
                title = pin.title,
                snippet = pin.snippet,
                draggable = true,
                onClick = {
                    deleteCandidate = pin
                    true
                },
                onInfoWindowLongClick = {
                    Toast.makeText(context, "Funca", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }

    if (pending != null) {
        AddPinDialog(
            title = title,
            onTitleChange = { title = it },
            snippet = snippet,
            onSnippetChange = { snippet = it },
            onConfirm = {
                viewmodel.confirmAdd(title.ifBlank { "Marcador" }, snippet.ifBlank { null })
            },
            onDismiss = { viewmodel.cancelAdd() },
        )
    }
}

@Composable
fun RequestLocationPermission() {
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = RequestPermission(),
        ) {}
    Box(Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            },
        ) { Text("Solicitar Permiso de Ubicacion") }
    }
}
