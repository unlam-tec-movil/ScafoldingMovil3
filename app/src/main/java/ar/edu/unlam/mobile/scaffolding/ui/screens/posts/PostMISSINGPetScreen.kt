package ar.edu.unlam.mobile.scaffolding.ui.screens.posts

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.data.models.Gender
import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import ar.edu.unlam.mobile.scaffolding.data.models.Type
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMissingPetScreen(
    onBackClick: () -> Unit = {},
    onFinishClick: () -> Unit = {},
    viewModel: PostViewModel = hiltViewModel(),
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
                    onBackClick = onBackClick,
                    onFinishClick = {
                        Log.d("PostScreen", "Botón Finalizar clickeado")

                        if (name.isNotBlank() &&
                            seenAt.isNotBlank() &&
                            locality.isNotBlank() &&
                            selectedGender != null &&
                            selectedType != null
                        ) {
                            val pet =
                                Pet(
                                    name = name,
                                    seenAt = seenAt,
                                    locality = locality,
                                    gender = selectedGender!!,
                                    type = selectedType!!,
                                )
                            Log.d("PostScreen", "Datos de mascota listos: $pet")

                            viewModel.savePet(pet, photoUri) { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                onFinishClick()
                            }
                        } else {
                            Toast.makeText(context, "Faltan completar campos", Toast.LENGTH_SHORT).show()
                            Log.e("PostScreen", "Campos faltantes")
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
                    text = "Completa los datos de tu mascota.",
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
                PetTextField(value = name, onValueChange = { name = it }, placeholder = "Nombre...")
                Spacer(Modifier.height(12.dp))
                PetTextField(
                    value = seenAt,
                    onValueChange = { seenAt = it },
                    placeholder = "Visto por última vez en...",
                )
                Spacer(Modifier.height(12.dp))
                PetTextField(
                    value = locality,
                    onValueChange = { locality = it },
                    placeholder = "Localidad...",
                )

                Spacer(Modifier.height(16.dp))

                // GÉNERO
                PetGenderSelector(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    genderOptions = listOf(Gender.MALE, Gender.FEMALE),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMissingPetTopBar(
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp),
                )
            }
        },
        actions = {
            TextButton(onClick = onFinishClick) {
                Text(
                    text = "Finalizar",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorTwo),
    )
}

@Composable
fun PetPhotoUploader(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            onImageSelected(uri)
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(30.dp))
                .clickable { launcher.launch("image/*") }
                .background(Color.White, shape = RoundedCornerShape(30.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Foto de mascota",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Crop,
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Agregar fotos",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp),
                )
                Text("Añade una foto", color = Color.Gray)
            }
        }
    }
}

@Composable
fun PetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorTwo,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
    )
}

@Composable
fun PetGenderSelector(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit,
    genderOptions: List<Gender> = Gender.entries.toList(),
) {
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

@Composable
fun PetTypeSelector(
    selectedType: Type?,
    onTypeSelected: (Type) -> Unit,
) {
    val typeOptions = Type.entries.toTypedArray()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        typeOptions.forEach { option ->
            FilterChip(
                selected = selectedType == option,
                onClick = { onTypeSelected(option) },
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
                        .height(56.dp)
                        .width(110.dp)
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

@Composable
fun CirculoDecorativo(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .width(1000.dp)
                .height(280.dp)
                .graphicsLayer {
                    scaleX = 1.5f
                }.background(
                    color = ColorTwo,
                    shape = RoundedCornerShape(180.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
    }
}
