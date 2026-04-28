package ni.edu.uam.practica2704

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PurpleDark = Color(0xFF190019)
val RoyalViolet = Color(0xFF9303C5)
val OrangeVibrant = Color(0xFFFF5722)

private val LightColorScheme = lightColorScheme(
    primary = PurpleDark,
    secondary = RoyalViolet,
    // ...existing code...
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

