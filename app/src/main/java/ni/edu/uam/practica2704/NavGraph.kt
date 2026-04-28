package ni.edu.uam.practica2704

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel: ProfileViewModel = viewModel()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController, viewModel) }
        composable("createProfile") { CreateProfileScreen(navController, viewModel) }
        composable("profile") { ProfileScreen(navController, viewModel) }
        composable("settings") { SettingsScreen(navController) }
        composable("messages") { MessagesScreen(navController) }
    }
}

