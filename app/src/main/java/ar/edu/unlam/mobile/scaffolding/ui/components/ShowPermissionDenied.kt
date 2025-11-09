package ar.edu.unlam.mobile.scaffolding.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

@Composable
@OptIn(ExperimentalPermissionsApi::class)
 fun ShowPermissionDenied(
    isFirstLoad: Boolean,
    status: PermissionStatus.Denied,
    context: Context,
    locationPermissionState: PermissionState
) {
    if (isFirstLoad) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Si ya no es primera carga (usuario rechazó), mostrar mensaje explicativo
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
                        // Abrir configuración de la app
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        context.startActivity(intent)
                    } else {
                        // Volver a pedir el permiso
                        locationPermissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(if (isBlocked) "Abrir Configuración" else "Conceder permiso")
            }
        }
    }
    return // No mostrar el mapa hasta que haya permiso
}
