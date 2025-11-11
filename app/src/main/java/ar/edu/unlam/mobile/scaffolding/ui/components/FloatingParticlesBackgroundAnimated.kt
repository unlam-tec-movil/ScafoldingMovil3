package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
    val id: Int,
    val radius: Float,
    val color: Color,
    val baseX: Float,
    val baseY: Float,
    val speedX: Int,
    val speedY: Int,
    val pulseSpeed: Int,
)

@Composable
fun FloatingParticlesBackgroundAnimated(
    modifier: Modifier = Modifier,
    particleCount: Int = 25,
    excludeTopPx: Float = 0f,
) {
    val random = remember { Random(System.currentTimeMillis()) }

    // partículas con posiciones base aleatorias y tamaños más grandes
    val particles =
        remember {
            List(particleCount) { i ->
                Particle(
                    id = i,
                    radius = random.nextInt(10, 22).toFloat(), // 🔹 tamaño más visible
                    color = Color(0xFFD81B60).copy(alpha = listOf(0.12f, 0.18f, 0.25f).random(random)),
                    baseX = random.nextFloat(),
                    baseY = random.nextFloat(),
                    speedX = random.nextInt(4000, 8000),
                    speedY = random.nextInt(5000, 10000),
                    pulseSpeed = random.nextInt(2500, 5000),
                )
            }
        }

    val transition = rememberInfiniteTransition(label = "particles")

    // Animaciones de posición y pulso (tamaño variable)
    val animatedStates =
        particles.map { p ->
            val xAnim =
                transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(p.speedX, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "x_${p.id}",
                )

            val yAnim =
                transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(p.speedY, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "y_${p.id}",
                )

            val pulse =
                transition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.1f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(p.pulseSpeed, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "pulse_${p.id}",
                )

            Triple(xAnim, yAnim, pulse)
        }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height - excludeTopPx

        particles.forEachIndexed { i, p ->
            val (xAnim, yAnim, pulse) = animatedStates[i]
            val x = (p.baseX + (xAnim.value - 0.5f) * 0.6f).coerceIn(0f, 1f) * width
            val y =
                excludeTopPx + (p.baseY + (yAnim.value - 0.5f) * 0.6f)
                    .coerceIn(0f, 1f) * height

            drawCircle(
                color = p.color,
                radius = p.radius * pulse.value,
                center = Offset(x, y),
            )
        }
    }
}
