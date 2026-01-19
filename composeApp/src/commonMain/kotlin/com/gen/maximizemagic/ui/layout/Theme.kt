package com.gen.maximizemagic.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import maximizemagic.composeapp.generated.resources.Res
import maximizemagic.composeapp.generated.resources.Magic // Asegúrate que el nombre coincida con tu archivo ttf
import org.jetbrains.compose.resources.Font

// --- COLORES NUEVOS ---
val MagicDeepBlue = Color(0xFF003087) // Azul Disney
val MagicGold = Color(0xFFD4AF37)     // Dorado Real
val MagicCeleste = Color(0xFF81D4FA)  // Celeste suave
val MagicBackground = Color(0xFFF0F9FF) // Fondo muy claro
val MagicSurface = Color(0xFFFFFFFF)   // Color para Cards

private val LightColorScheme = lightColorScheme(
    primary = MagicDeepBlue,
    onPrimary = Color.White,
    secondary = MagicGold,
    onSecondary = Color.Black,
    background = MagicBackground,
    surface = MagicSurface,
    surfaceVariant = MagicCeleste.copy(alpha = 0.2f), // Para fondos de cards suaves
    primaryContainer = MagicDeepBlue,
    onPrimaryContainer = MagicGold
)

@Composable
fun MaximizeMagicTheme(content: @Composable () -> Unit) {
    // CARGAR FUENTE DISNEY
    val disneyFamily = FontFamily(Font(Res.font.Magic))

    val typography = Typography(
        // Para títulos grandes (TopBar, Logo)
        headlineLarge = TextStyle(
            fontFamily = disneyFamily,
            fontSize = 32.sp,
            color = MagicGold
        ),
        // Para títulos de Cards
        titleMedium = TextStyle(
            fontFamily = disneyFamily,
            fontSize = 22.sp,
            color = MagicDeepBlue
        ),
        // Para cuerpo de texto (Horarios, descripción)
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default, // Fuente estándar para lectura fácil
            fontSize = 16.sp
        )
    )

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = typography,
        content = content
    )
}