package ar.edu.unlam.mobile.scaffolding.ui.screens.petdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.screens.search.SEARCH_ROUTE

const val PET_DETAIL_ROUTE = "pet_detail"

/**
 * Pantalla temporal de detalle de mascota.
 * Solo tiene un botón para navegar al SearchScreen.
 */
@Composable
fun PetDetailScreen(
    petId: String,
    navController: NavController,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Detalle de Mascota",
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = "ID: $petId",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Button(
            onClick = {
                navController.navigate("$SEARCH_ROUTE/$petId")
            },
            modifier = Modifier.padding(top = 32.dp),
        ) {
            Text("Guiarme hasta el lugar")
        }
    }
}
