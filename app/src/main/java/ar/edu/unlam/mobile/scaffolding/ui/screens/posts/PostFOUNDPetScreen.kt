package ar.edu.unlam.mobile.scaffolding.ui.screens.posts

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.data.models.Gender
import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import ar.edu.unlam.mobile.scaffolding.data.models.Status
import ar.edu.unlam.mobile.scaffolding.data.models.Type
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFoundPet(
    navController: NavController,
    postViewModel: PostViewModel,
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var seenAt by remember { mutableStateOf("") }
    var locality by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var selectedType by remember { mutableStateOf<Type?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        CirculoDecorativo(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
        )

        Scaffold(
            topBar = {
                PostMissingPetTopBar(
                    onBackClick = { navController.popBackStack() },
                    onFinishClick = {
                        if (
                            seenAt.isNotBlank() &&
                            locality.isNotBlank() &&
                            selectedGender != null &&
                            selectedType != null
                        ) {
                            val pet =
                                Pet(
                                    name = "",
                                    seenAt = seenAt,
                                    locality = locality,
                                    gender = selectedGender!!,
                                    type = selectedType!!,
                                    status = Status.FOUND,
                                )

                            Log.d("PostScreen", "Datos de mascota listos: $pet")

                            postViewModel.savePet(pet, photoUri) { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                                navController.navigate("feed") {
                                    popUpTo("feed_screen") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            Toast.makeText(context, "Faltan completar campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            },
            containerColor = Color.Transparent,
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
            ) {
                Text(
                    text =
                        "Completa los datos de " +
                            "la mascota encontrada.",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                // FOTO
                PetPhotoUploader(
                    selectedImageUri = photoUri,
                    onImageSelected = { uri -> photoUri = uri },
                )

                Spacer(Modifier.height(12.dp))

                // CAMPOS DE TEXTO
                Spacer(Modifier.height(12.dp))
                PetTextField(
                    value = seenAt,
                    onValueChange = { seenAt = it },
                    placeholder = "Visto en...",
                )
                Spacer(Modifier.height(12.dp))
                PetTextField(
                    value = locality,
                    onValueChange = { locality = it },
                    placeholder = "Localidad...",
                )

                Spacer(Modifier.height(16.dp))

                // GÉNERO
                foundPetGenderSelector(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                )

                Spacer(Modifier.height(16.dp))

                // TIPO
                PetTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                )
            }
        }
    }
}

@Composable
fun foundPetGenderSelector(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit,
) {
    val genderOptions = Gender.entries.toTypedArray()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        genderOptions.forEach { option ->
            FilterChip(
                selected = selectedGender == option,
                onClick = { onGenderSelected(option) },
                shape = RoundedCornerShape(30.dp),
                colors =
                    FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = ColorTwo,
                        labelColor = Color.Black,
                        selectedLabelColor = Color.White,
                    ),
                modifier =
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                label = {
                    Text(
                        text = option.label,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            )
        }
    }
}
