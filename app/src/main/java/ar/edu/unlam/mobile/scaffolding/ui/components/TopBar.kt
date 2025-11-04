package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onMenuClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White,
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.White,
                )
            }
        },
        actions = actions,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color(0xFFE91E63), // rosa frambuesa arriba
                                Color(0xFFF8BBD0), // rosa pastel abajo
                            ),
                    ),
                ),
        colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
            ),
    )
}
