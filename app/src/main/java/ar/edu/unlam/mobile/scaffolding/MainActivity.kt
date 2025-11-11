package ar.edu.unlam.mobile.scaffolding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.screens.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.RegisterScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.feed.FeedScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.map.MapScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.editProfile.EditProfile
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    ComponentActivity(),
    ActivityResultCallback<Any> {
    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel = PostViewModel()
        setContent {
            ScaffoldingV2Theme {
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background,
//                ) {
                AppNavHost()
            }
        }
    }

    val requestPermissonLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                setPermissionsText()
            }
        }

    private fun hasCammeraPermission(): Boolean = true

    private fun setPermissionsText() {
    }

    override fun onActivityResult(result: Any) {
        TODO("Not yet implemented")
    }

    fun onRequestPermissionsResult() {
    }
}

@Composable
fun MainScreen() {
    val controller = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = {
            val currentRoute =
                controller
                    .currentBackStackEntryAsState()
                    .value
                    ?.destination
                    ?.route
            if (currentRoute != "splash") {
                BottomBar(controller = controller)
            }
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar { Text(data.visuals.message) }
            }
        },
    ) { paddingValue ->

        NavHost(
            navController = controller,
            startDestination = "feed",
            modifier = Modifier.padding(paddingValue),
        ) {
            composable("feed") {
                FeedScreen()
            }

            composable("map") {
                MapScreen()
            }

            composable("edit") {
                EditProfile(controller)
            }

            composable(
                "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                UserScreen(
                    onDetallesClick = { /* si necesitás navegar a detalles personales */ },
                    onMascotasClick = { controller.navigate("mis_mascotas/$id") },
                    onReportesClick = { controller.navigate("mis_reportes/$id") },
                    onLogoutClick = {
                        FirebaseAuth.getInstance().signOut()
                        controller.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                )
            }

            // Opcional: rutas destino para mascotas/reportes
            composable(
                "mis_mascotas/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            ) { /* MisMascotasScreen(controller) */ }

            composable(
                "mis_reportes/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            ) { /* MisReportesScreen(controller) */ }
        }
    }
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) "main" else "login"

    NavHost(
        navController = nav,
        startDestination = startDestination,
    ) {
//
//        composable("splash") {
//            Splash(
//                onFinish = {
//                    val next = if (FirebaseAuth.getInstance().currentUser != null) {
//                        "main"
//                    } else {
//                        "login"
//                    }
//
//                    nav.navigate(next) {
//                        popUpTo("splash") { inclusive = true }
//                    }
//                }
//            )
//        }

        composable("login") {
            LoginScreen(
                onRegisterClick = { nav.navigate("register") },
                onLoginSuccess = {
                    nav.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }

        composable("register") {
            RegisterScreen(
                onBackClick = { nav.popBackStack() },
                onRegisterSuccess = {
                    nav.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
            )
        }

        composable("main") {
            MainScreen()
        }
    }
}

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun MainScreen() {
//    // Controller es el elemento que nos permite navegar entre pantallas. Tiene las acciones
//    // para navegar como naviegate y también la información de en dónde se "encuentra" el usuario
//    // a través del back stack
//    val controller = rememberNavController()
//    val snackBarHostState = remember { SnackbarHostState() }
//    val currentBackStackEntry = controller.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry.value?.destination?.route
//    val showBottomBar = currentRoute != "splash"
//
//    Scaffold(
// //        topBar = {
// //            TopAppBar(
// //                title = { Text("Petapp pa") },
// //                navigationIcon = {
// //                    Button(onClick = {}, modifier = Modifier) {
// //                        Icon(
// //                            imageVector = Icons.Default.Menu,
// //                            contentDescription = "Filters",
// //                        )
// //                    }
// //                },
// //            )
// //        },
//
//        bottomBar ={
//            if (showBottomBar && currentRoute != "login" && currentRoute != "register") {
//                 BottomBar(
//                     controller = controller,
//                     )
//            }
//            },
// //        floatingActionButton = {
// //            IconButton(onClick = { controller.navigate("home") }) {
// //                var icon by remember { mutableStateOf(Icons.Default.Add) }
// //
// //                Icon(
// //                    icon,
// //                    contentDescription = "Home",
// //                    Modifier.clickable {
// //                        if (icon == Icons.Default.Add) {
// //                            icon = Icons.Default.Close
// //                        } else {
// //                            icon = Icons.Default.Add
// //                        }
// //                    },
// //                )
// //            }
// //        },
//        snackbarHost = {
//            SnackbarHost(snackBarHostState) { data ->
//                // custom snackbar with the custom action button color and border
//                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
//                val buttonColor =
//                    if (isError) {
//                        ButtonDefaults.textButtonColors(
//                            containerColor = MaterialTheme.colorScheme.errorContainer,
//                            contentColor = MaterialTheme.colorScheme.error,
//                        )
//                    } else {
//                        ButtonDefaults.textButtonColors(
//                            contentColor = MaterialTheme.colorScheme.inversePrimary,
//                        )
//                    }
//
//                Snackbar(
//                    modifier =
//                        Modifier.border(2.dp, MaterialTheme.colorScheme.secondary).padding(12.dp),
//                    action = {
//                        TextButton(
//                            onClick = { if (isError) data.dismiss() else data.performAction() },
//                            colors = buttonColor,
//                        ) {
//                            Text(data.visuals.actionLabel ?: "")
//                        }
//                    },
//                ) {
//                    Text(data.visuals.message)
//                }
//            }
//        },
//    ) { paddingValue ->
//        // NavHost es el componente que funciona como contenedor de los otros componentes que
//        // podrán ser destinos de navegación.
//        NavHost(navController = controller, startDestination = LOGIN_SCREEN_ROUTE) {
//            // composable es el componente que se usa para definir un destino de navegación.
//            // Por parámetro recibe la ruta que se utilizará para navegar a dicho destino.
//
//            composable("login") {
//                // Home es el componente en sí que es el destino de navegación.
//                HomeScreen(modifier = Modifier.padding(paddingValue))
//            }
//
//            composable("login") {
//                LoginScreen(
//                    onRegisterClick = {
//                        controller.navigate("register")
//                    },
//                    onLoginSuccess = {
//                        controller.navigate("main") {
//                            popUpTo("login") { inclusive = true }
//                        }
//                    }
//                )
//
//            }
//
//            composable(
//                route = "user/{id}",
//                arguments = listOf(navArgument("id") { type = NavType.StringType }),
//            ) { navBackStackEntry ->
//                val id = navBackStackEntry.arguments?.getString("id") ?: "1"
//                UserScreen(controller)
//            }
//            composable("edit") {
//                EditProfile(controller)
//            }
//        }
//    }
// }
