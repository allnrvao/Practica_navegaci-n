package ni.edu.uam.practica2704

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.practica2704.screens.LoginScreen
import ni.edu.uam.practica2704.screens.HomeScreen
import ni.edu.uam.practica2704.screens.CreateProfileScreen
import ni.edu.uam.practica2704.screens.ProfileScreen
import ni.edu.uam.practica2704.screens.SettingsScreen
import ni.edu.uam.practica2704.screens.MessagesScreen


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

