package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.scaffolding.ui.screens.FORM_ROUTE
import ar.edu.unlam.mobile.scaffolding.ui.screens.map.MAP_ROUTE

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    NavigationBar {
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "feed" } == true,
            onClick = { controller.navigate("feed") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Feed",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == FORM_ROUTE } == true,
            onClick = { controller.navigate(MAP_ROUTE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "User",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "user/{id}" } == true,
            onClick = { controller.navigate("user/usuario") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )
    }
}

@Preview
@Composable
fun caca() {
    val controller = rememberNavController()

    BottomBar(controller)
}
