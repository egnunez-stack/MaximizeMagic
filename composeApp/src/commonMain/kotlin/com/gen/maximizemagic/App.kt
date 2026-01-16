package com.gen.maximizemagic

import androidx.compose.foundation.layout.*
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
import com.gen.maximizemagic.network.ParkApi
import com.gen.maximizemagic.network.OrlandoWeather

data class ParkInfo(
    val id: String,
    val openingHours: String,
    val closingHours: String,
    val tollPlazaCoords: String
)

enum class Screen { Welcome, Parks, Detail, Settings, Agenda } // AÑADIDO: Agenda

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var userName by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf<String?>(null) }
    var showWelcomeMessage by remember { mutableStateOf(false) }
    var selectedParkId by remember { mutableStateOf("") }
    var selectedParkName by remember { mutableStateOf("") }
    var orlandoWeather by remember { mutableStateOf<OrlandoWeather?>(null) }

    val authManager = remember { AuthManager() }
    val api = remember { ParkApi() }

    LaunchedEffect(Unit) {
        orlandoWeather = api.getOrlandoFullWeather()
    }

    LaunchedEffect(showWelcomeMessage) {
        if (showWelcomeMessage) {
            delay(5000)
            showWelcomeMessage = false
        }
    }

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
                        weatherInfoFromApp = orlandoWeather,
                        onNavigateToDetail = { name: String, info: ParkInfo ->
                            selectedParkName = name
                            selectedParkId = info.id
                            currentScreen = Screen.Detail
                        },
                        onNavigateToSettings = { currentScreen = Screen.Settings },
                        onBack = { currentScreen = Screen.Welcome }
                    )
                }
                Screen.Detail -> {
                    ParkDetailScreen(
                        parkId = selectedParkId,
                        parkName = selectedParkName,
                        userPhotoUrl = userPhotoUrl,
                        onBack = { currentScreen = Screen.Parks }
                    )
                }
                Screen.Settings -> {
                    SettingsScreen(
                        userPhotoUrl = userPhotoUrl,
                        onNavigateToAgenda = { currentScreen = Screen.Agenda }, // CALLBACK AÑADIDO
                        onBack = { currentScreen = Screen.Parks }
                    )
                }
                Screen.Agenda -> { // NUEVA PANTALLA
                    AgendaScreen(
                        userPhotoUrl = userPhotoUrl,
                        onBack = { currentScreen = Screen.Settings }
                    )
                }
            }

            if (showWelcomeMessage) {
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Text("¡Bienvenido, $userName! ✨", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}