package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.domain.model.Pet
import ar.edu.unlam.mobile.scaffolding.ui.components.BackFloatingButton
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingParticlesBackgroundAnimated

@Composable
fun UserPetsScreen(
    viewModel: UserPetsViewModel,
    petIds: List<String>,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    val state = viewModel.uiState

    // Cargar mascotas al entrar
    LaunchedEffect(petIds) {
        viewModel.loadUserPets(petIds)
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFFF2F3F5)),
    ) {
        FloatingParticlesBackgroundAnimated(
            particleCount = 24,
            excludeTopPx = with(androidx.compose.ui.platform.LocalDensity.current) { 68.dp.toPx() },
            modifier = Modifier.fillMaxSize(),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CurvedTopBar(title = "Mis mascotas")
            Spacer(modifier = Modifier.height(70.dp))

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.error}")
                    }
                }
                state.pets.isNotEmpty() -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        state.pets.forEach { pet ->
                            PetCard(pet)
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes mascotas registradas")
                    }
                }
            }
        }

        // FAB flotante para volver
        BackFloatingButton(
            onClick = onNavigateBack,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
        )
    }
}

@Composable
fun PetCard(pet: Pet) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(pet.name, style = MaterialTheme.typography.titleMedium, color = Color(0xFF3C3C3C))
            Text("Tipo: ${pet.type}", style = MaterialTheme.typography.bodyMedium)
            // ⚠️ Solo mostrar edad si existe en tu modelo Pet
            // Text("Edad: ${pet.age}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
