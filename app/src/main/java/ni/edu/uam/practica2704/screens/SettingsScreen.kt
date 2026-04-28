package ni.edu.uam.practica2704.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo Oscuro")
            Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
        }
        // ...existing code...
    }
}

