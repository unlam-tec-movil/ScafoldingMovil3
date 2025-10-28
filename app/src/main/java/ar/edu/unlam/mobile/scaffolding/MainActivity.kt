package ar.edu.unlam.mobile.scaffolding

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.components.SnackbarVisualsWithError
import ar.edu.unlam.mobile.scaffolding.ui.screens.FormScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.HomeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.map.MapScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.editProfile.EditProfile
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ActivityResultCallback<Any> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }

    val requestPermissonLauncher =  registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        isGranted ->
        if(isGranted){
            setPermissionsText()
        }
    }


    private fun hasCammeraPermission(): Boolean {


        return true
    }

    private fun setPermissionsText(){


    }
    override fun onActivityResult(result: Any) {
        TODO("Not yet implemented")
    }


    fun onRequestPermissionsResult(){

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    // Controller es el elemento que nos permite navegar entre pantallas. Tiene las acciones
    // para navegar como naviegate y también la información de en dónde se "encuentra" el usuario
    // a través del back stack
    val controller = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Petapp pa")},
            navigationIcon = {
                Button(onClick = {}, modifier = Modifier ){
                    Icon(imageVector = Icons.Default.Menu,
                        contentDescription = "Filters")
                }
            }
        )  }
        ,
        bottomBar = { BottomBar(controller = controller) },
        floatingActionButton = {
            IconButton(onClick = { controller.navigate("home") }) {
                var icon by remember { mutableStateOf(Icons.Default.Add) }

                Icon(icon, contentDescription = "Home",
                    Modifier.clickable{
                        if(icon == Icons.Default.Add){
                            icon = Icons.Default.Close
                        }
                        else{
                            icon = Icons.Default.Add
                        }
                    }

                    )
            }
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                // custom snackbar with the custom action button color and border
                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
                val buttonColor =
                    if (isError) {
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.inversePrimary,
                        )
                    }

                Snackbar(
                    modifier =
                        Modifier.border(2.dp, MaterialTheme.colorScheme.secondary).padding(12.dp),
                    action = {
                        TextButton(
                            onClick = { if (isError) data.dismiss() else data.performAction() },
                            colors = buttonColor,
                        ) {
                            Text(data.visuals.actionLabel ?: "")
                        }
                    },
                ) {
                    Text(data.visuals.message)
                }
            }
        },
    ) { paddingValue ->
        // NavHost es el componente que funciona como contenedor de los otros componentes que
        // podrán ser destinos de navegación.
        NavHost(navController = controller, startDestination = HOME_SCREEN_ROUTE) {
            // composable es el componente que se usa para definir un destino de navegación.
            // Por parámetro recibe la ruta que se utilizará para navegar a dicho destino.

            composable("home") {
                // Home es el componente en sí que es el destino de navegación.
                HomeScreen(modifier = Modifier.padding(paddingValue))
            }

            composable("map") {
                MapScreen()
            }

            composable(
                route = "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id") ?: "1"
                UserScreen(controller)
            }
            composable("edit") {
                EditProfile(controller)
            }
        }
    }
}
