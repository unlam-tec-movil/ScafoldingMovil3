package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.domain.model.User
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorOne
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo
import ar.edu.unlam.mobile.scaffolding.ui.theme.DarkBlue
import ar.edu.unlam.mobile.scaffolding.ui.theme.PetFinderFont

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(ColorOne, ColorTwo),
                            start = Offset(0f, 50f),
                            end = Offset(0f, 1400f),
                        ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleSection()
            Spacer(modifier = Modifier.height(70.dp))
            InputSection(
                email = email,
                onEmailChange = { email = it },
                phone = phone,
                onPhoneChange = { phone = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
            )
            Spacer(modifier = Modifier.height(70.dp))

            CreateButton(
                email = email,
                phone = phone,
                password = password,
                confirmPassword = confirmPassword,
                context = context,
                onRegisterUser = { email, password, user ->
                    viewModel.registerUser(
                        email = email,
                        password = password,
                        user = user,
                        onSuccessMessage = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        },
                        onErrorMessage = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                    )
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            LoginText(onBackClick)
        }
    }
}

@Composable
fun TitleSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Register",
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            fontFamily = PetFinderFont,
            color = Color.White,
        )
        Text(
            text = "¡Crea una cuenta para comenzar!",
            fontFamily = PetFinderFont,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}

@Composable
fun InputSection(
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ShadowedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "E-mail",
            leadingIcon = Icons.Default.Person,
        )
        ShadowedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            placeholder = "Teléfono",
            leadingIcon = Icons.Default.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )
        ShadowedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Contraseña",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray,
                    )
                }
            },
            isPasswordVisible = isPasswordVisible,
        )
        ShadowedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Confirmar contraseña",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray,
                    )
                }
            },
            isPasswordVisible = isPasswordVisible,
        )
    }
}

@Composable
fun ShadowedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPasswordVisible: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = trailingIcon,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        modifier =
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    val shadowColor = Color.Black.copy(alpha = 0.25f)
                    val cornerRadius = 30.dp.toPx()
                    val offsetY = 8.dp.toPx()
                    drawRoundRect(
                        color = shadowColor,
                        topLeft = Offset(0f, offsetY),
                        size = this.size,
                        cornerRadius =
                            androidx.compose.ui.geometry
                                .CornerRadius(cornerRadius, cornerRadius),
                    )
                },
        shape = RoundedCornerShape(30.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFEFEEEE),
                unfocusedBorderColor = Color(0xFFEFEEEE),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color(0xFFEFEEEE),
                unfocusedContainerColor = Color(0xFFEFEEEE),
            ),
        singleLine = true,
    )
}

@Composable
fun CreateButton(
    email: String,
    phone: String,
    password: String,
    confirmPassword: String,
    context: Context,
    onRegisterUser: (String, String, User) -> Unit,
) {
    Button(
        onClick = {
            when {
                email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_LONG).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                }
                else -> {
                    val user =
                        User(
                            id = "", // El id se asignará después del registro en Firebase Auth
                            email = email,
                            phone = phone,
                            postIds = emptyList(), // Usuario nuevo no tiene posts todavía
                        )
                    onRegisterUser(email, password, user)
                }
            }
        },
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
            text = "Crear",
            fontSize = 18.sp,
            fontFamily = PetFinderFont,
        )
    }
}

@Composable
fun LoginText(onBackClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "¿Ya tienes una cuenta? ", color = Color.White)
        TextButton(onClick = { onBackClick }) {
            Text(
                text = "Inicia sesión aquí",
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}
