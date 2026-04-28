package ni.edu.uam.practica2704.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(navController: NavController) {
    val tabs = listOf("Recibidos", "Enviados", "Solicitudes")
    var selectedTab by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }
        when (selectedTab) {
            0 -> Text("Mensajes recibidos")
            1 -> Text("Mensajes enviados")
            2 -> Text("Solicitudes")
        }
    }
}

