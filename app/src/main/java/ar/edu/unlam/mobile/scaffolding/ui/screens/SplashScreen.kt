package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.theme.PetFinderFont
import kotlinx.coroutines.delay

const val SPLASH_SCREEN = "splash"

@Preview
@Composable
fun SplashScreen() {
    Splash(onFinish = {})
}

@Composable
fun Splash(onFinish: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(500)
        scale.animateTo(
            targetValue = 1f,
            animationSpec =
                tween(
                    durationMillis = 1000,
                    easing = {
                        OvershootInterpolator(5f).getInterpolation(it)
                    },
                ),
        )
        onFinish()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Splash screen background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.pet_finder_logo),
                contentDescription = "Dog face",
                modifier =
                    Modifier
                        .size(100.dp)
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                        ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PetFinder",
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                fontFamily = PetFinderFont,
                color = Color.White,
                modifier =
                    Modifier.graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                    ),
            )
        }
    }
}
