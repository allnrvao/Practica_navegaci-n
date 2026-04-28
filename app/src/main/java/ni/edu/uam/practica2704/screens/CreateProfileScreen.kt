package ni.edu.uam.practica2704.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ni.edu.uam.practica2704.Profile
import ni.edu.uam.practica2704.ProfileViewModel
import android.net.Uri

@Composable
fun CreateProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") } // Nuevo
    var username by remember { mutableStateOf("") } // Nuevo
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    // Cambiamos el estado a Uri? para que coincida con el modelo
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") })
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") })

        // ... (resto de tus campos de categorías y chips) ...

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (name.isNotBlank() && username.isNotBlank()) {
                    viewModel.saveProfile(
                        Profile(
                            name = name,
                            lastName = lastName,    // Agregado
                            username = username,    // Agregado
                            email = email,
                            // Si el error dice que esperaba String pero enviaste Int:
                            age = age,              // Pásalo como String directamente
                            photoUri = photoUri,    // Ahora es tipo Uri?
                            interests = selectedInterests.toList()
                        )
                    )
                    navController.popBackStack()
                }
            }
        ) {
            Text("Guardar")
        }
    }
}