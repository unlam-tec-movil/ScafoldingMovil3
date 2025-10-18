package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.scaffolding.ui.screens.FORM_ROUTE

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "home" } == true,
            onClick = { controller.navigate("home") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = "Home",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // Ícono activo bien visible
                    unselectedIconColor = Color(0xFFFFEBEE), // Rosa pálido, suave y legible
                    indicatorColor = Color.White.copy(alpha = 0.2f), // Fondo del ítem activo, sutil
                ),
        )
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "user/{id}" } == true,
            onClick = { controller.navigate("user/usuario") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "User",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // Ícono activo bien visible
                    unselectedIconColor = Color(0xFFFFEBEE), // Rosa pálido, suave y legible
                    indicatorColor = Color.White.copy(alpha = 0.2f), // Fondo del ítem activo, sutil
                ),
        )
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == FORM_ROUTE } == true,
            onClick = { controller.navigate(FORM_ROUTE) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // Ícono activo bien visible
                    unselectedIconColor = Color(0xFFFFEBEE), // Rosa pálido, suave y legible
                    indicatorColor = Color.White.copy(alpha = 0.2f), // Fondo del ítem activo, sutil
                ),
        )
    }
}
