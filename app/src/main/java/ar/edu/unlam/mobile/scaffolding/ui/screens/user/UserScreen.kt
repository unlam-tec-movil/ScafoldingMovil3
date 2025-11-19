package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingParticlesBackgroundAnimated

@Composable
fun UserScreen(
    onDetallesClick: () -> Unit,
    onMascotasClick: () -> Unit,
    onReportesClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val topbarHeightDp = 68.dp
    val topbarExclusionPx = with(LocalDensity.current) { topbarHeightDp.toPx() }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F3F5)),
    ) {
        FloatingParticlesBackgroundAnimated(
            particleCount = 24,
            excludeTopPx = topbarExclusionPx,
            modifier =
                Modifier
                    .fillMaxSize()
                    .zIndex(-1f),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CurvedTopBar(title = "Configuración")

            Spacer(modifier = Modifier.height(70.dp))

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .weight(1f),
                verticalArrangement = Arrangement.Top,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                ) {
                    ItemCard("Detalles personales", onClick = onDetallesClick)
                    ItemCard("Mis mascotas", onClick = onMascotasClick)
                    ItemCard("Mis reportes", onClick = onReportesClick)
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LogoutButton(onClick = onLogoutClick)
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

// ---------- TopBar ----------
@Composable
fun CurvedTopBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    val curveDepthPx = with(LocalDensity.current) { 28.dp.toPx() }
    val shadowHeightPx = with(LocalDensity.current) { 32.dp.toPx() }
    val startYOffset = with(LocalDensity.current) { 8.dp.toPx() }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(68.dp)
                .drawBehind {
                    val shadowBrush =
                        Brush.verticalGradient(
                            colors = listOf(Color(0x22000000), Color.Transparent),
                            startY = size.height - startYOffset,
                            endY = size.height + shadowHeightPx,
                        )
                    val shadowPath =
                        Path().apply {
                            moveTo(0f, size.height - curveDepthPx / 2f)
                            quadraticBezierTo(
                                size.width / 2f,
                                size.height + curveDepthPx * 1.2f,
                                size.width,
                                size.height - curveDepthPx / 2f,
                            )
                            lineTo(size.width, size.height + shadowHeightPx)
                            lineTo(0f, size.height + shadowHeightPx)
                            close()
                        }
                    drawPath(path = shadowPath, brush = shadowBrush, style = Fill)

                    val path =
                        Path().apply {
                            moveTo(0f, 0f)
                            lineTo(0f, size.height - curveDepthPx)
                            quadraticBezierTo(
                                size.width / 2f,
                                size.height + curveDepthPx * 1.1f,
                                size.width,
                                size.height - curveDepthPx,
                            )
                            lineTo(size.width, 0f)
                            close()
                        }
                    drawPath(path, color = Color.White, style = Fill)
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(title, color = Color(0xFFD81B60), fontWeight = FontWeight.Bold, fontSize = 24.sp)
    }
}

// ---------- Card de item ----------
@Composable
fun ItemCard(
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, fontSize = 16.sp, color = Color(0xFF3C3C3C))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFB0B0B0),
            )
        }
    }
}

// ---------- Botón de logout ----------
@Composable
fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.5.dp, Color(0xFFD81B60)),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFFD81B60),
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                tint = Color(0xFFD81B60),
                modifier =
                    Modifier
                        .size(20.dp)
                        .graphicsLayer { scaleX = -1f },
            )
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "Log out",
                    fontSize = 16.sp,
                    color = Color(0xFFD81B60),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserScreen() {
    UserScreen({}, {}, {}, {})
}
