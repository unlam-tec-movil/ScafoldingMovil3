package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorOne
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo
import ar.edu.unlam.mobile.scaffolding.ui.theme.PetFinderFont
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.VisualTransformation
import ar.edu.unlam.mobile.scaffolding.data.models.User
import ar.edu.unlam.mobile.scaffolding.ui.theme.DarkBlue

@Composable
fun RegisterScreen(onRegisterUser: (User) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ColorOne, ColorTwo), // Ejemplo
                    start = Offset(0f, 50f),
                    end = Offset(0f, 1400f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleSection()
            Spacer(modifier = Modifier.height(70.dp))
            InputSection(
                name = name,
                onNameChange = { name = it },
                phone = phone,
                onPhoneChange = { phone = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
            )
            Spacer(modifier = Modifier.height(70.dp))
            CreateButton(
                name = name,
                phone = phone,
                password = password,
                confirmPassword = confirmPassword,
                onRegisterUser = onRegisterUser,
                context = context)
            Spacer(modifier = Modifier.height(16.dp))
            LoginText()
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
            color = Color.White
        )
        Text(
            text = "¡Crea una cuenta para comenzar!",
            fontFamily = PetFinderFont,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Composable
fun InputSection(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ShadowedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Nombre",
            leadingIcon = Icons.Default.Person
        )

        ShadowedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            placeholder = "Teléfono",
            leadingIcon = Icons.Default.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
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
                        tint = Color.Gray
                    )
                }
            },
            isPasswordVisible = isPasswordVisible
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
                        tint = Color.Gray
                    )
                }
            },
            isPasswordVisible = isPasswordVisible
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = trailingIcon,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val shadowColor = Color.Black.copy(alpha = 0.25f)
                val cornerRadius = 30.dp.toPx()
                val offsetY = 8.dp.toPx()

                drawRoundRect(
                    color = shadowColor,
                    topLeft = Offset(0f, offsetY),
                    size = this.size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                )
            },
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFEFEEEE),
            unfocusedBorderColor = Color(0xFFEFEEEE),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color(0xFFEFEEEE),
            unfocusedContainerColor = Color(0xFFEFEEEE),
        ),
        singleLine = true
    )
}

@Composable
fun CreateButton(
    name: String,
    phone: String,
    password: String,
    confirmPassword: String,
    onRegisterUser: (User) -> Unit,
    context: Context
) {
    Button(
        onClick = {
            when {
                name.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_LONG).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                }
                else -> {
                    val user = User(name = name, phone = phone, password = password)
                    onRegisterUser(user)
                }
            }},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(55.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkBlue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp)
    ) {
        Text(
            text = "Crear",
            fontSize = 18.sp,
            fontFamily = PetFinderFont
        )
    }
}

@Composable
fun LoginText() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "¿Ya tienes una cuenta? ", color = Color.White)
        TextButton(onClick = { /* TODO: Navegar a login */ }) {
            Text(
                text = "Inicia sesión aquí",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewRegisterScreen() {
//    RegisterScreen()
//}