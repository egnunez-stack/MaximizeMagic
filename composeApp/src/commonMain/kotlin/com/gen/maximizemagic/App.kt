package com.gen.maximizemagic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.gen.maximizemagic.model.*

// Clase de datos para centralizar la info de cada parque
data class ParkInfo(
    val id: String,
    val openingHours: String,
    val closingHours: String,
    val tollPlazaCoords: String // Latitud y Longitud para el mapa
)

enum class Screen {
    Welcome,
    Parks,
    Detail
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var selectedParkId by remember { mutableStateOf("") }
    var selectedParkName by remember { mutableStateOf("") }

    // Diccionario completo con IDs de API, Horarios y Coordenadas de Peajes (Toll Plazas)
    // Se añadió Universal Epic Universe
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
        when (currentScreen) {
            Screen.Welcome -> {
                MaximizeMagicScreen(
                    onConnectClick = { currentScreen = Screen.Parks },
                    onExitClick = {
                        // Cerramos la aplicación usando la función nativa

                    }
                )
            }
            Screen.Parks -> {
                ThemeParksScreen(
                    parksMap = parksData,
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
    }
}