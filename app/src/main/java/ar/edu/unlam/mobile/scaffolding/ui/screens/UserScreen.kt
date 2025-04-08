package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ar.edu.unlam.mobile.scaffolding.ui.components.Greeting

@Composable
fun UserScreen(userId: String) {
    Greeting(userId)
    Text("Texto de prueba")
}
