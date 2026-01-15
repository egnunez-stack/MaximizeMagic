package com.gen.maximizemagic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.gen.maximizemagic.model.*
import com.gen.maximizemagic.ui.MaximizeMagicScreen

// --- 1. DEFINICIÓN DE CLASE DE DATOS ---
data class ParkInfo(
    val id: String,
    val openingHours: String,
    val closingHours: String,
    val tollPlazaCoords: String
)

// Definición de las pantallas disponibles
enum class Screen {
    Welcome,
    Parks,
    Detail
}

@Composable
fun App() {
    // Estados de navegación y usuario
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var userName by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf<String?>(null) }
    var showWelcomeMessage by remember { mutableStateOf(false) }

    // Estados para la selección de parque
    var selectedParkId by remember { mutableStateOf("") }
    var selectedParkName by remember { mutableStateOf("") }

    val authManager = remember { AuthManager() }

    // Datos de los parques (Orlando)
    val parksData = remember {
        mapOf(
            "Magic Kingdom" to ParkInfo("6", "09:00", "23:00", "28.405,-81.579"),
            "Animal Kingdom" to ParkInfo("8", "08:00", "19:00", "28.359,-81.591"),
            "Disney Hollywood Studios" to ParkInfo("7", "09:00", "21:00", "28.352,-81.561"),
            "Epcot" to ParkInfo("5", "09:00", "21:00", "28.369,-81.544"),
            "Universal Studios Florida" to ParkInfo("65", "09:00", "21:00", "28.473,-81.465"),
            "Islands of Adventure" to ParkInfo("64", "09:00", "21:00", "28.473,-81.465"),
            "Universal Epic Universe" to ParkInfo("epic-wiki", "09:00", "21:00", "28.438,-81.452")
        )
    }

    // Efecto para cerrar el cartel de bienvenida automáticamente tras 3 segundos
    LaunchedEffect(showWelcomeMessage) {
        if (showWelcomeMessage) {
            delay(3000)
            showWelcomeMessage = false
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.Welcome -> {
                    MaximizeMagicScreen(
                        onConnectClick = {
                            authManager.signInWithGoogle { success, name, photo ->
                                if (success) {
                                    userName = name ?: "Usuario"
                                    userPhotoUrl = photo
                                    showWelcomeMessage = true
                                    currentScreen = Screen.Parks
                                }
                            }
                        },
                        onExitClick = { closeApp() }
                    )
                }
                Screen.Parks -> {
                    ThemeParksScreen(
                        parksMap = parksData,
                        userPhotoUrl = userPhotoUrl,
                        onNavigateToDetail = { name, info ->
                            selectedParkName = name
                            selectedParkId = info.id
                            currentScreen = Screen.Detail
                        },
                        onBack = {
                            currentScreen = Screen.Welcome
                        }
                    )
                }
                Screen.Detail -> {
                    ParkDetailScreen(
                        parkId = selectedParkId,
                        parkName = selectedParkName,
                        onBack = {
                            currentScreen = Screen.Parks
                        }
                    )
                }
            }

            // Cartel de bienvenida flotante (Toast personalizado)
            if (showWelcomeMessage) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Text(
                        "¡Bienvenido, $userName! ✨",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}