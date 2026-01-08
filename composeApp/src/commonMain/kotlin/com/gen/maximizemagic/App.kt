package com.gen.maximizemagic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.gen.maximizemagic.model.*

// Enumeración para manejar el flujo de navegación entre pantallas
enum class Screen {
    Welcome,
    Parks,
    Detail
}

@Composable
fun App() {
    // Estados para controlar la navegación y la selección del parque
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var selectedParkId by remember { mutableStateOf("") }
    var selectedParkName by remember { mutableStateOf("") }

    // IDs reales de la API Queue-Times para Orlando, FL
    val parkIds = mapOf(
        "Magic Kingdom" to "6",
        "Animal Kingdom" to "8",
        "Disney Hollywood Studios" to "7",
        "Universal Studios" to "65",
        "Universal Island of Adventures" to "64",
        "Universal Epic" to "65" // ID temporal
    )

    MaterialTheme {
        when (currentScreen) {
            Screen.Welcome -> {
                MaximizeMagicScreen(
                    onConnectClick = {
                        currentScreen = Screen.Parks
                    },
                    onExitClick = {
                        println("Saliendo de la app...")
                    }
                )
            }
            Screen.Parks -> {
                ThemeParksScreen(onParkClick = { parkName ->
                    selectedParkName = parkName
                    selectedParkId = parkIds[parkName] ?: ""
                    currentScreen = Screen.Detail
                })
            }
            Screen.Detail -> {
                ParkDetailScreen(
                    parkId = selectedParkId,
                    parkName = selectedParkName,
                    onBack = {
                        // Cambiamos el estado para regresar a la lista de parques
                        currentScreen = Screen.Parks
                    }
                )
            }
        }
    }
}
