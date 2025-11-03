package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.editProfile.EDIT_PROFILE_ROUTE

@Composable
fun UserScreen(
    controller: NavHostController,
    viewModel: UserViewModel = hiltViewModel(),
) {
    Column(
        Modifier
            .background(Color.Gray)
            .fillMaxWidth(),
    ) {
        Icon(
            Icons.Filled.Edit,
            "EditProfile",
            modifier =
                Modifier
                    .align(Alignment.End)
                    .size(50.dp)
                    .offset(y = 50.dp)
                    .clickable { controller.navigate(EDIT_PROFILE_ROUTE) },
        )
        Spacer(Modifier.height(90.dp))
        Icon(
            Icons.Filled.Person,
            "User",
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(0.1.dp, Color.White, CircleShape)
                    .size(100.dp),
        )

        Spacer(Modifier.height(50.dp))
        Text(
            "Nombre de usuario",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
        )
    }
}

@Preview
@Composable
fun Screen() {
    val controller: NavHostController = rememberNavController()
    UserScreen(controller)
}
