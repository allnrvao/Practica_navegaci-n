package ni.edu.uam.practica2704.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ni.edu.uam.practica2704.Profile
import ni.edu.uam.practica2704.ProfileViewModel

@Composable
fun CreateProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    var name by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        // ...existing code...
        Text("Categorías")
        val categories = mapOf("Académico" to listOf("Matemáticas", "Ciencias"), "Hobbies" to listOf("Deportes"), "Social" to listOf("Amigos"))
        categories.forEach { (cat, interests) ->
            Text(cat)
            Row { interests.forEach { FilterChip(selected = it in selectedInterests, onClick = { selectedInterests = if (it in selectedInterests) selectedInterests - it else selectedInterests + it }, label = { Text(it) }) } }
        }
        Button(onClick = {
            if (name.isNotBlank() && photoUri != null) {
                viewModel.saveProfile(Profile(name, photoUri, selectedInterests.toList()))
                navController.popBackStack("profile", false)
            }
        }) { Text("Guardar") }
    }
}

