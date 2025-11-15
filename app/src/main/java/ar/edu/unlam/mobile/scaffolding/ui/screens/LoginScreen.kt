package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorOne
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo
import ar.edu.unlam.mobile.scaffolding.ui.theme.DarkBlue
import ar.edu.unlam.mobile.scaffolding.ui.theme.PetFinderFont
import kotlin.math.min

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            onLoginSuccess()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(ColorOne, ColorTwo),
                            start = Offset(0.0f, 50.0f),
                            end = Offset(0.0f, 1400.0f),
                        ),
                ),
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_login_ears),
            contentDescription = "Orejas",
        )

        LoginCard(
            onRegisterClick = onRegisterClick,
            onLoginClick = { email, password ->
                viewModel.login(email, password)
            },
        )
    }
}

@Composable
fun LoginCard(
    onRegisterClick: () -> Unit,
    onLoginClick: (String, String) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    var isFocusedPassword by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .offset(y = (-45).dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DogsFace(textLength = email.length, isFocused = isFocused)
            Spacer(modifier = Modifier.height(2.dp))

            Box(contentAlignment = Alignment.Center) {
                CampoDeTexto(
                    texto = email,
                    onTextChange = { email = it },
                    onFocusChange = { focused -> isFocused = focused },
                )
                Hands(
                    isFocused = isFocusedPassword,
                    isPasswordVisible = isPasswordVisible,
                    isPasswordFocused = isFocusedPassword,
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            CampoDeContrasenia(
                texto = password,
                onTextChange = { password = it },
                isVisible = isPasswordVisible,
                onVisibilityChange = { isPasswordVisible = it },
                onFocusChange = { focused -> isFocusedPassword = focused },
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LoginButton(
                onRegisterClick = onRegisterClick,
                onLoginClick = { onLoginClick(email, password) },
            )
        }
    }
}

@Composable
fun LoginButton(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Button(
        onClick = onLoginClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(55.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = DarkBlue,
                contentColor = Color.White,
            ),
        shape = RoundedCornerShape(30.dp),
    ) {
        Text(
            text = "Iniciar sesión",
            fontFamily = PetFinderFont,
            fontSize = 18.sp,
        )
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onRegisterClick) {
            Text(
                "Regístrate",
                color = Color.White,
                fontFamily = PetFinderFont,
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
fun CampoDeContrasenia(
    texto: String,
    onTextChange: (String) -> Unit,
    isVisible: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onVisibilityChange: (Boolean) -> Unit,
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextChange,
        shape = RoundedCornerShape(30.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFEFEEEE),
                unfocusedBorderColor = Color(0xFFEFEEEE),
                focusedTextColor = Color(0xFF000000),
                unfocusedTextColor = Color(0xFF000000),
                focusedContainerColor = Color(0xFFEFEEEE),
                unfocusedContainerColor = Color(0xFFEFEEEE),
            ),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isVisible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = Color.Gray,
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier =
            Modifier
                .onFocusChanged { focusState -> onFocusChange(focusState.isFocused) }
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
        placeholder = { Text("Contraseña") },
    )
}

@Composable
fun DogsFace(
    textLength: Int,
    isFocused: Boolean,
) {
    val typingRotation = if (isFocused) min(textLength * 1f, 20f) else 0f
    val focusRotation = if (isFocused) -10f else 0f

    val targetRotation = typingRotation + focusRotation

    val rotation by animateFloatAsState(targetValue = targetRotation)

    val infiniteTransition = rememberInfiniteTransition()
    val stretch by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.8f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(300, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
    )

    Box(
        modifier =
            Modifier
                .offset(y = (-80).dp)
                .size(200.dp)
                .graphicsLayer {
                    rotationZ = -rotation
                },
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_login_face),
            contentDescription = "Dog face",
            modifier = Modifier.fillMaxSize(),
        )

        Image(
            painter = painterResource(id = R.drawable.dog_login_tongue),
            contentDescription = "Tongue",
            modifier =
                Modifier
                    .size(70.dp)
                    .offset(y = 170.dp)
                    .graphicsLayer(
                        scaleY = stretch,
                        scaleX = 1f,
                        transformOrigin = TransformOrigin(0.5f, 0f),
                    ),
        )
    }
}

@Composable
fun CampoDeTexto(
    texto: String,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextChange,
        shape = RoundedCornerShape(30.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFEFEEEE),
                unfocusedBorderColor = Color(0xFFEFEEEE),
                focusedTextColor = Color(0xFF000000),
                unfocusedTextColor = Color(0xFF000000),
                focusedContainerColor = Color(0xFFEFEEEE),
                unfocusedContainerColor = Color(0xFFEFEEEE),
            ),
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState -> onFocusChange(focusState.isFocused) },
        placeholder = { Text("E-mail") },
    )
}

@Composable
fun Hands(
    isFocused: Boolean,
    isPasswordVisible: Boolean,
    isPasswordFocused: Boolean,
) {
    val offsetY by animateDpAsState(
        targetValue =
            when {
                isPasswordFocused && !isPasswordVisible -> (-284).dp
                isPasswordFocused && isPasswordVisible -> (-120).dp
                else -> (-20).dp
            },
        animationSpec = tween(durationMillis = 400),
    )

    val rotationLeftHand by animateFloatAsState(
        targetValue = if (isFocused) 210f else 0f,
        animationSpec = tween(durationMillis = 400),
    )

    val rotationRightHand by animateFloatAsState(
        targetValue = if (isFocused) -210f else 0f,
        animationSpec = tween(durationMillis = 400),
    )

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_login_hand),
            contentDescription = "Left hand",
            modifier = Modifier.size(80.dp).offset(y = offsetY).graphicsLayer(rotationZ = rotationLeftHand),
        )
        Spacer(modifier = Modifier.width(70.dp))

        Image(
            painter = painterResource(id = R.drawable.dog_login_hand),
            contentDescription = "Right hand",
            modifier =
                Modifier
                    .size(80.dp)
                    .offset(y = offsetY)
                    .graphicsLayer(rotationZ = rotationRightHand),
        )
    }
}
