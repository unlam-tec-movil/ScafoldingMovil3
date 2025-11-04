package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.editProfile.EDIT_PROFILE_ROUTE

@Composable
fun UserScreen(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel(),
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFCE4EC), Color.White),
                        startY = 0f,
                        endY = 1200f,
                    ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .shadow(6.dp, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFF616161),
                    modifier = Modifier.size(64.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nombre de usuario",
                fontSize = 22.sp,
                color = Color(0xFF424242),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "usuario@ejemplo.com",
                fontSize = 16.sp,
                color = Color(0xFF757575),
            )
        }

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar perfil",
            tint = Color(0xFF616161),
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(28.dp)
                    .clickable { controller.navigate(EDIT_PROFILE_ROUTE) },
        )
    }
}
