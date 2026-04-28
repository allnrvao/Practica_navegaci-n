package ni.edu.uam.practica2704.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ni.edu.uam.practica2704.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        profile?.let {
            Text("Nombre: ${it.name}")
            // ...existing code...
        }
        Button(onClick = { navController.navigate("settings") }) { Text("Ajustes") }
    }
}

