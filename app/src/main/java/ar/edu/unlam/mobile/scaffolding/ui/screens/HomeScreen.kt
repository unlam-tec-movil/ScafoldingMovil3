package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.ui.components.Greeting
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // La información que obtenemos desde el view model la consumimos a través de un estado de
    // "tres vías": Loading, Success y Error. Esto nos permite mostrar un estado de carga,
    // un estado de éxito y un mensaje de error.
    val uiState: HomeUIState by viewModel.uiState.collectAsState()

    when (val helloState = uiState.helloMessageState) {
        is HelloMessageUIState.Loading -> {
            // Loading
        }

        is HelloMessageUIState.Success -> {
            Column {
                Greeting(helloState.message, modifier)
                Text("Prueba")
                AsyncImage(
                    model = "https://as2.ftcdn.net/jpg/04/20/53/13/1000_F_420531310_w0Pcyga9y4bgQ1f4nQU0sS53C5k1V29H.jpg",
                    contentDescription = "Imagen de ejemplo",
                    contentScale = ContentScale.Crop // opcional
                )

            }

        }

        is HelloMessageUIState.Error -> {
            // Error
        }
    }
}
