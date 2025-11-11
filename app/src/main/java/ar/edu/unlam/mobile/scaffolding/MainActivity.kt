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
import androidx.hilt.navigation.compose.hiltViewModel
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
import ar.edu.unlam.mobile.scaffolding.ui.screens.map.MapPostScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.map.MapScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostFoundPet
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostMissingPetScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.posts.PostViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.editProfile.EditProfile
import ar.edu.unlam.mobile.scaffolding.ui.screens.userPosts.MyPetsScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    ComponentActivity(),
    ActivityResultCallback<Any> {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContent {
            ScaffoldingV2Theme {
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background,
//                ) {
                AppNavHost()
                // PostMissingPetScreen()
                // PostFoundPet()
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
    val postViewModel = hiltViewModel<PostViewModel>()

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
                FeedScreen(navController = controller, postViewModel = postViewModel)
            }

            composable("map_post_screen") {
                MapPostScreen(navController = controller, postViewModel = postViewModel)
            }

            composable("post_missing_pet_screen") {
                PostMissingPetScreen(navController = controller, postViewModel = postViewModel)
            }

            composable("post_found_pet_screen") {
                PostFoundPet(navController = controller, postViewModel = postViewModel)
            }

            composable("map") {
                MapScreen()
            }

            composable("edit") {
                EditProfile(controller)
            }

            composable("misposts") {
                MyPetsScreen()
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
