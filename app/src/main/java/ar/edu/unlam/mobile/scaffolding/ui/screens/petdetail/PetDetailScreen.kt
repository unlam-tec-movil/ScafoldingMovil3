package ar.edu.unlam.mobile.scaffolding.ui.screens.petdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.screens.search.SEARCH_ROUTE
import coil.compose.AsyncImage

const val PET_DETAIL_ROUTE = "pet_detail"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String,
    navController: NavController,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PetDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    val pet by viewModel.pet.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 1 })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.White,
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.White,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(top = innerPadding.calculateTopPadding() * 0),
                contentAlignment = Alignment.BottomCenter,
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AsyncImage(
                        model = pet?.imageUrl,
                        contentDescription = "Pet Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Row(
                    Modifier
                        .height(20.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) {
                                Color.White
                            } else {
                                Color.White.copy(
                                    alpha = 0.5f,
                                )
                            }
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color),
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = pet?.name ?: "Zeus",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "Hace 2 dia(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pet?.locality ?: "Buenos Aires",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    DetailRow(
                        label = "Visto última vez en:",
                        value = pet?.seenAt ?: "Dirección 1000",
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(label = "Perdido desde:", value = "03/09/2025")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(label = "Sexo:", value = pet?.gender?.label ?: "Unknown")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(label = "Teléfono:", value = pet?.phoneNumber ?: "11 1234 5678")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val buttonColor = Color(0xFFC2185B)

                    WhatsAppButton(
                        phoneNumber = pet?.phoneNumber ?: "9 11 1234 5678",
                        petName = pet?.name ?: "Zeus",
                        buttonColor = buttonColor,
                        modifier = modifier,
                    )

                    Button(
                        onClick = { navController.navigate("$SEARCH_ROUTE/$petId") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = Color.White,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Buscar",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Buscar en mapa",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun WhatsAppButton(
    phoneNumber: String,
    petName: String,
    buttonColor: Color,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = { launchWhatsApp(context, phoneNumber, petName) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, buttonColor),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = buttonColor,
            ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.whatsapp_icon),
            contentDescription = "Contactar por WhatsApp",
            modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Contactar", fontWeight = FontWeight.Bold)
    }
}

private fun launchWhatsApp(
    context: Context,
    phoneNumber: String,
    petName: String,
) {
    try {
        val cleanNumber = "549" + phoneNumber.replace(Regex("[^0-9]"), "")

        val message = "Hola, vi tu publicación sobre $petName en la app."
        val encodedMessage = Uri.encode(message)

        val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage"
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                data = url.toUri()
            }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
