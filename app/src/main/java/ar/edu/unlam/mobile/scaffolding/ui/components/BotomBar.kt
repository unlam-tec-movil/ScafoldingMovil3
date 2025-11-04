package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val matchRoute: String,
    val navigateRoute: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
)

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = Color.Transparent,
        modifier =
            Modifier.background(
                Brush.verticalGradient(
                    colors =
                        listOf(
                            Color(0xFFF8BBD0), // rosa pastel abajo
                            Color(0xFFE91E63), // rosa frambuesa arriba
                        ),
                ),
            ),
    ) {
        val items =
            listOf(
                BottomNavItem("home", "home", Icons.Default.Home, "Inicio"),
                BottomNavItem("map", "map", Icons.Default.Map, "Mapa"),
                BottomNavItem("user", "user/usuario", Icons.Default.Person, "Perfil"),
            )

        items.forEach { item ->
            val selected =
                navBackStackEntry?.destination?.hierarchy?.any {
                    it.route?.startsWith(item.matchRoute) == true
                } == true

            val iconColor by animateColorAsState(
                if (selected) Color.White else Color(0xFFFFCDD2),
            )
            val iconSize by animateDpAsState(if (selected) 32.dp else 24.dp)
            val borderSize by animateDpAsState(if (selected) 2.dp else 0.dp)
            val borderColor by animateColorAsState(
                if (selected) Color(0xFFF8BBD0) else Color.Transparent,
            )

            NavigationBarItem(
                selected = selected,
                onClick = { controller.navigate(item.navigateRoute) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier =
                            Modifier
                                .size(iconSize)
                                .border(
                                    width = borderSize,
                                    color = borderColor,
                                    shape = CircleShape,
                                ),
                    )
                },
                alwaysShowLabel = false,
            )
        }
    }
}
