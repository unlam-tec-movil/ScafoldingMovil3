package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.domain.model.User
import ar.edu.unlam.mobile.scaffolding.ui.components.BackFloatingButton
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingParticlesBackgroundAnimated

@Composable
fun PersonalDetailsScreen(
    viewModel: PersonalDetailsViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val state = viewModel.uiState

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F5)),
    ) {
        // Fondo animado
        FloatingParticlesBackgroundAnimated(
            particleCount = 24,
            excludeTopPx = with(androidx.compose.ui.platform.LocalDensity.current) { 68.dp.toPx() },
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Topbar curva
            CurvedTopBar(title = "Detalles personales")

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
                state.user != null -> {
                    val user = state.user
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        DetailCard(label = "Email", value = user.email)
                        DetailCard(label = "Mascotas registradas", value = "${user.postIds.size}")
                        DetailCard(label = "Teléfono", value = user.phone)
                    }
                }
            }
        }

        // 👉 FAB flotante abajo a la derecha
        BackFloatingButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}

// ---------- Card de detalle ----------
@Composable
fun DetailCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(label, fontSize = 14.sp, color = Color(0xFF888888))
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF3C3C3C))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalDetailsPreview() {
    val fakeUser = User(
        id = "123",
        email = "juan@example.com",
        phone = "+54 9 11 1234 5678",
        postIds = listOf("p1", "p2")
    )
    val fakeState = PersonalDetailsUiState(isLoading = false, user = fakeUser)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F5)),
    ) {
        FloatingParticlesBackgroundAnimated(
            particleCount = 24,
            excludeTopPx = with(androidx.compose.ui.platform.LocalDensity.current) { 68.dp.toPx() },
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.fillMaxSize()) {
            CurvedTopBar(title = "Detalles personales")
            Spacer(modifier = Modifier.height(70.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                DetailCard(label = "Email", value = fakeUser.email)
                DetailCard(label = "Mascotas registradas", value = "${fakeUser.postIds.size}")
                DetailCard(label = "Teléfono", value = fakeUser.phone)
            }
        }

        // FAB en el preview
        BackFloatingButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}






