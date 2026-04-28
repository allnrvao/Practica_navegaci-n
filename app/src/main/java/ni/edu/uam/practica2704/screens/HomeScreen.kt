package ni.edu.uam.practica2704.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ni.edu.uam.practica2704.ProfileViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { navController.navigate("messages") }) {
                Icon(Icons.Rounded.Send, contentDescription = "Mensajes")
            }
        }
        // Simular posts
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Post de ejemplo")
                Row {
                    repeat(5) { Icon(Icons.Rounded.Star, contentDescription = null) } // Estrellas
                }
                Button(onClick = { /* Simular respuesta */ }) { Text("Responder") }
            }
        }
        // ...existing code...
    }
}

