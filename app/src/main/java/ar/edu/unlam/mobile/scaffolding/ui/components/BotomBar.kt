package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.scaffolding.ui.screens.map.MAP_ROUTE

// Única definición de BottomNavItem
data class BottomNavItem(
    val matchRoute: String,
    val navigateRoute: String,
    val icon: ImageVector,
    val label: String,
)

@Composable
fun BottomBar(
    controller: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val gradientBrush =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFFF3AFC9), // rosa más fuerte (abajo)
                    Color(0xFFFCE3ED), // más claro (arriba)
                ),
        )

    val accent = Color(0xFFD81B60)
    val inactive = Color(0xFF8E8E8E)

    Surface(
        modifier =
            modifier
                .zIndex(1f)
                .offset(y = (-8).dp)
                .shadow(10.dp, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)),
        color = Color.Transparent,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .background(brush = gradientBrush)
                    .fillMaxWidth()
                    .height(72.dp),
            contentAlignment = Alignment.Center,
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
            ) {
                val items =
                    listOf(
                        BottomNavItem("feed", "feed", Icons.Default.Home, "Feed"),
                        BottomNavItem(MAP_ROUTE, MAP_ROUTE, Icons.Default.Map, "Map"),
                        BottomNavItem("user/{id}", "user/usuario", Icons.Default.Person, "User"),
                    )

                items.forEach { item ->
                    val selected = currentRoute?.contains(item.matchRoute.substringBefore("/")) == true
                    val iconColor by animateColorAsState(if (selected) Color.White else inactive)
                    val backgroundColor by animateColorAsState(if (selected) accent else Color.Transparent)
                    val paddingAnim by animateDpAsState(if (selected) 6.dp else 10.dp)

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            controller.navigate(item.navigateRoute) {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Box(
                                modifier =
                                    Modifier
                                        .background(
                                            color = backgroundColor,
                                            shape = RoundedCornerShape(14.dp),
                                        ).padding(paddingAnim),
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = iconColor,
                                )
                            }
                        },
                        colors =
                            NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = accent,
                                unselectedIconColor = inactive,
                            ),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    val controller = rememberNavController()
    BottomBar(controller = controller)
}
