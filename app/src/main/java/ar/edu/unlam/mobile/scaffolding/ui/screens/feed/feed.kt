package ar.edu.unlam.mobile.scaffolding.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.data.models.Gender
import ar.edu.unlam.mobile.scaffolding.data.models.Pet
import ar.edu.unlam.mobile.scaffolding.data.models.Type
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostCard
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostViewModel
import ar.edu.unlam.mobile.scaffolding.ui.theme.ColorTwo
import ar.edu.unlam.mobile.scaffolding.ui.theme.PetFinderFont
import ar.edu.unlam.mobile.scaffolding.ui.theme.SoftGray
import kotlin.String
import kotlin.Unit

const val FEED_SCREEN_ROUTE = "feed"

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FeedScreen(viewModel: PostViewModel = hiltViewModel()) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showSheet by remember { mutableStateOf(false) }

    val pets by viewModel.pets.collectAsState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(SoftGray),
    ) {
        CirXD(
            modifier =
                Modifier
                    .padding(top = 16.dp)
                    .offset(y = (-450).dp),
        )

        AppName(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 15.dp),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(90.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp),
            ) {
                PerdidosEncontradosButtons()
                Spacer(modifier = Modifier.weight(1f))
                FilterButton(onClick = { showSheet = true })
            }
            PetFeed(pets = pets)
        }

        PublishButton(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 130.dp),
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = null,
            modifier = Modifier.fillMaxHeight(0.9f),
        ) {
            FilterContent(onClose = { showSheet = false })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetFeed(pets: List<Pet>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(pets) { pet ->
            PostCard(pet = pet)
        }
    }
}

@Composable
fun FilterButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Filtro",
            modifier = Modifier.size(40.dp),
            tint = ColorTwo,
        )
    }
}

@Composable
fun FilterContent(onClose: () -> Unit) {
    var selectedType by remember { mutableStateOf<Type?>(null) }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var locality by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
    ) {
        Text(
            "Filtrar por:",
            color = ColorTwo,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        Text("Tipo:", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TypeButtons(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Género:", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GenderButtons(
                selectedGender = selectedGender,
                onGenderSelected = { selectedGender = it },
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("Localidad:", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Locality(
                value = locality,
                onValueChange = { locality = it },
            )
        }
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onClose,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorTwo),
            ) {
                Text("Aplicar", color = Color.White)
            }
            Button(
                onClick = onClose,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAB7C7)),
            ) {
                Text("Limpiar", color = Color(0xFFD92656))
            }
        }
    }
}

@Composable
fun Locality(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "Localidad") },
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
fun TypeButtons(
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
fun GenderButtons(
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
                        .weight(2f)
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
fun PerdidosEncontradosButtons() {
    var selectedButton by remember { mutableStateOf("Perdidos") }

    val colorPerdidosBackground by animateColorAsState(
        targetValue = if (selectedButton == "Perdidos") ColorTwo else Color.White,
        animationSpec = tween(durationMillis = 400),
    )
    val colorPerdidosText by animateColorAsState(
        targetValue = if (selectedButton == "Perdidos") Color.White else ColorTwo,
        animationSpec = tween(durationMillis = 400),
    )

    val colorEncontradosBackground by animateColorAsState(
        targetValue = if (selectedButton == "Encontrados") ColorTwo else Color.White,
        animationSpec = tween(durationMillis = 400),
    )
    val colorEncontradosText by animateColorAsState(
        targetValue = if (selectedButton == "Encontrados") Color.White else ColorTwo,
        animationSpec = tween(durationMillis = 400),
    )

    Button(
        onClick = { selectedButton = "Perdidos" },
        shape = RoundedCornerShape(50.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = colorPerdidosBackground,
                contentColor = colorPerdidosText,
            ),
    ) {
        Text("Perdidos")
    }

    Button(
        onClick = { selectedButton = "Encontrados" },
        shape = RoundedCornerShape(50.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = colorEncontradosBackground,
                contentColor = colorEncontradosText,
            ),
    ) {
        Text("Encontrados")
    }
}

@Composable
fun AppName(modifier: Modifier = Modifier) {
    Text(
        text = "PetFinder",
        color = ColorTwo,
        fontFamily = PetFinderFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        modifier = modifier,
    )
}

@Composable
fun CirXD(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .width(1000.dp)
                .height(500.dp)
                .graphicsLayer {
                    scaleX = 1.5f
                }.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(180.dp),
                ).background(
                    color = Color.White,
                    shape = RoundedCornerShape(180.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
    }
}

@Composable
fun PublishButton(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "rotation",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd,
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter =
                scaleIn(
                    initialScale = 0.5f,
                    animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                ) + fadeIn(),
            exit =
                scaleOut(
                    targetScale = 0.5f,
                    animationSpec = tween(durationMillis = 150),
                ) + fadeOut(),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(bottom = 90.dp)
                        .background(ColorTwo.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
            ) {
                TextButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.HeartBroken,
                        contentDescription = "Perdí a mi mascota",
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Perdí a mi mascota", color = Color.White)
                }
                TextButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Vi una mascota perdida",
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vi una mascota perdida", color = Color.White)
                }
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = ColorTwo,
            shape = RoundedCornerShape(50),
            modifier = Modifier.size(70.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.rotate(rotation).size(40.dp),
            )
        }
    }
}
