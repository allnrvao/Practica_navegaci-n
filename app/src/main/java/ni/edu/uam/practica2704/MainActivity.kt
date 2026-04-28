package ni.edu.uam.practica2704

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import ni.edu.uam.practica2704.ui.theme.Practica2704Theme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.horizontalScroll
import androidx.lifecycle.viewmodel.compose.viewModel

// ---------- COLORES PERSONALIZADOS ----------
val DeepPurple = Color(0xFF190019)
val CoralOrange = Color(0xFFFF7F50)
val PalePink = Color(0xFFDFB6B2)

// ---------- DATA CLASSES ----------

data class Post(
    val title: String,
    val description: String,
    val category: String,
    val author: String,
    val date: String, // e.g., "2023-10-01"
    val averageRating: Float,
    val replies: List<Message>
)

data class Message(
    val sender: String,
    val content: String,
    val timestamp: String
)

// ---------- INTERESES POR CATEGORÍAS ----------
val academicInterests = listOf("Programación", "Cálculo", "Diseño")
val hobbyInterests = listOf("Música", "Películas", "Gaming")
val socialInterests = listOf("Relaciones", "Networking")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val viewModel: ProfileViewModel = viewModel()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(PurpleDark, Color.Black)))
                ) {
                    MyApp()
                }
            }
        }
    }
}

// ---------- APP PRINCIPAL ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    var profile by remember { mutableStateOf<Profile?>(null) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var darkTheme by remember { mutableStateOf(true) } // For settings switch

    Practica2704Theme(darkTheme = darkTheme) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "login"
        ) {
            composable("login") {
                LoginScreen(navController) { isLoggedIn = true }
            }
            composable("home") {
                HomeScreen(navController, profile, posts)
            }
            composable("detail") {
                DetailScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController, profile) { navController.navigate("create_profile") }
            }
            composable("create_profile") {
                val profile = /* obtén el perfil actual si quieres editar */
                    CreateProfileScreen(
                        navController = navController,
                        currentProfile = profile,           // pasa el perfil actual
                        onSave = { newProfile ->
                            profile = newProfile            // actualizas el estado
                            navController.popBackStack()
                        }
                    )
            }
            composable("create_post") {
                CreatePostScreen(navController, profile?.username ?: "Anónimo") { newPost ->
                    posts = posts + newPost
                    navController.popBackStack()
                }
            }
            composable("messages") {
                MessagesScreen(navController)
            }
            composable("settings") {
                SettingsScreen(navController, darkTheme) { darkTheme = it }
            }
        }
    }
}

// ---------- PANTALLA DE LOGIN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(DeepPurple, Color(0xFF9303C5), CoralOrange, Color.Black),
                    center = Offset.Infinite
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .graphicsLayer(alpha = 0.7f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar Sesión - UAM MentorLink",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text("Recordarme", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { /* Forgot Password */ }) {
                        Text("Olvidé mi contraseña")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (username == "admin" && password == "admin123") {
                            onLoginSuccess()
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            errorMessage = "Usuario o contraseña incorrectos"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralOrange)
                ) {
                    Text("Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { /* Sign Up */ }) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}

// ---------- PANTALLA HOME CON BOTTOM NAV ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, profile: Profile?, posts: List<Post>) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Add, contentDescription = "Create") },
                    label = { Text("Crear") },
                    selected = currentRoute == "create_post",
                    onClick = { navController.navigate("create_post") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
                    label = { Text("Perfil") },
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Feed de Tutorías - UAM MentorLink",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { navController.navigate("messages") }) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Messages")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Mentoria", "Apoyo", "Clases Particulares", "Clases Pagadas")

                categories.forEach { category ->
                    FilterChip(
                        selected = category in selectedCategories,
                        onClick = {
                            selectedCategories = if (category in selectedCategories) {
                                selectedCategories - category
                            } else {
                                selectedCategories + category
                            }
                        },
                        label = { Text(category) }
                    )
                }
                IconButton(onClick = { /* Filter by date */ }) {
                    Icon(Icons.Rounded.DateRange, contentDescription = "Filter by date")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (profile != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (profile.photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(profile.photoUri),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(64.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = profile.username,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val filteredPosts = if (selectedCategories.isEmpty()) posts else posts.filter { it.category in selectedCategories }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPosts) { post ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.Transparent, PalePink.copy(alpha = 0.1f))
                                )
                            ),
                        onClick = { /* Opcional: navegar a detalles del post */ }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val badgeColor = if (post.category.contains("Pagadas")) CoralOrange else PalePink
                            Surface(
                                color = badgeColor,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = post.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Por: ${post.author}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row {
                                    for (i in 1..5) {
                                        Icon(
                                            Icons.Rounded.Star,
                                            contentDescription = null,
                                            tint = if (i <= post.averageRating.toInt()) Color.Yellow else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = { /* Responder */ }) {
                                    Text("Responder")
                                }
                            }
                        }
                    }
                }
            }

            if (posts.isEmpty()) {
                Text(
                    text = "No hay publicaciones aún. ¡Crea la primera!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

// ---------- PANTALLA DETALLE (MURO DE LA FACULTAD) ----------
@Composable
fun DetailScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder para imagen
            Surface(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.Rounded.Info,
                    contentDescription = "Imagen de la facultad",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Muro de la Facultad - UAM MentorLink",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Misión: Formar profesionales integrales, innovadores y comprometidos con el desarrollo sostenible de Nicaragua, a través de una educación de calidad, investigación aplicada y extensión universitaria.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Logros Destacados:",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            val achievements = listOf(
                "Primer lugar en competencias nacionales de innovación.",
                "Más de 500 publicaciones científicas en los últimos años.",
                "Proyectos de extensión en comunidades rurales.",
                "Graduación de miles de profesionales exitosos."
            )

            achievements.forEach { achievement ->
                Text(
                    text = achievement,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}

// ---------- PANTALLA PERFIL ----------
@Composable
fun ProfileScreen(navController: NavController, profile: Profile?, onCreateProfile: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Mi Perfil - UAM MentorLink",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (profile == null) {
                Text(
                    text = "No tienes un perfil creado aún.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCreateProfile,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Perfil")
                }
            } else {
                if (profile.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.photoUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(120.dp).clip(CircleShape).align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Usuario: ${profile.username}", style = MaterialTheme.typography.bodyLarge)
                Text("Nombre: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
                Text("Apellidos: ${profile.lastName}", style = MaterialTheme.typography.bodyLarge)
                Text("Edad: ${profile.age}", style = MaterialTheme.typography.bodyLarge)
                Text("Email: ${profile.email}", style = MaterialTheme.typography.bodyLarge)
                Text("Intereses: ${profile.interests.joinToString(", ")}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { navController.navigate("create_profile") }) {
                    Text(if (profile == null) "Crear Perfil" else "Editar Perfil")
                }
            }
        }
    }
}

// ---------- PANTALLA CREAR PERFIL ----------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(
    navController: NavController,
    currentProfile: Profile?,           // perfil existente (para editar)
    onSave: (Profile) -> Unit
) {
    var username by remember { mutableStateOf(currentProfile?.username ?: "") }
    var name by remember { mutableStateOf(currentProfile?.name ?: "") }
    var lastName by remember { mutableStateOf(currentProfile?.lastName ?: "") }
    var age by remember { mutableStateOf(currentProfile?.age ?: "") }
    var email by remember { mutableStateOf(currentProfile?.email ?: "") }
    var photoUri by remember { mutableStateOf<Uri?>(currentProfile?.photoUri) }
    var selectedInterests by remember {
        mutableStateOf(currentProfile?.interests?.toSet() ?: emptySet())
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> photoUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = if (currentProfile == null) "Crear Perfil" else "Editar Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Foto
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Rounded.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Seleccionar Foto")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de texto (mantén los OutlinedTextField que ya tenías)
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nombre de Usuario") }, modifier = Modifier.fillMaxWidth())
        // ... agrega los demás campos (name, lastName, age, email) igual que antes

        Spacer(modifier = Modifier.height(16.dp))
        Text("Intereses:", style = MaterialTheme.typography.titleMedium)

        // Aquí mantén tus FlowRow con los FilterChip de academicInterests, hobbyInterests y socialInterests

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (username.isNotBlank() && name.isNotBlank()) {
                    val newProfile = Profile(
                        username = username,
                        name = name,
                        lastName = lastName,
                        interests = selectedInterests.toList(),
                        age = age,
                        email = email,
                        photoUri = photoUri
                    )
                    onSave(newProfile)
                } else {
                    // Opcional: mostrar un Toast o Snackbar de error
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Perfil")
        }
    }
}

// ---------- PANTALLA CREAR POST ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController, author: String, onSave: (Post) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf("Mentoria", "Apoyo", "Clases Particulares", "Clases Pagadas")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Crear Publicación - UAM MentorLink",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
                    val post = Post(title, description, category, author, "2023-10-01", 0f, emptyList())
                    onSave(post)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar")
        }
    }
}

// ---------- PANTALLA DE MENSAJES ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Recibidos", "Enviados", "Recibidos")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Mensajes - UAM MentorLink",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Recibidos
                    LazyColumn {
                        items(5) { // Placeholder
                            OutlinedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Mensaje de ejemplo", style = MaterialTheme.typography.bodyLarge)
                                    Text("Timestamp", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Enviados
                    Text("Enviados - Lista vacía", style = MaterialTheme.typography.bodyLarge)
                }
                2 -> {
                    // Recibidos again? Perhaps "Archivados" or something, but as per user "Mensajes Recibidos (al responder una app), Enviados y Recibidos"
                    // Perhaps it's "Mensajes Recibidos", "Enviados", "Recibidos" is duplicate, maybe "Archivados"
                    Text("Recibidos - Lista vacía", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// ---------- PANTALLA DE CONFIGURACIÓN ----------
@Composable
fun SettingsScreen(navController: NavController, darkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    var notifications by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Configuración - UAM MentorLink",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Opción para cambiar el tema
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Modo Oscuro",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = darkTheme,
                    onCheckedChange = { onThemeChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notificaciones
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Notificaciones",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = notifications,
                    onCheckedChange = { notifications = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacidad
            Text(
                text = "Privacidad",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Idioma
            Text(
                text = "Idioma",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cerrar Sesión
            Button(
                onClick = { /* Cerrar Sesión */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
