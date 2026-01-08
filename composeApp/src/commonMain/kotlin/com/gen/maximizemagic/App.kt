package com.gen.maximizemagic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.gen.maximizemagic.model.*
import com.gen.maximizemagic.ui.MaximizeMagicScreen

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

    // IDs reales de la API themeparks.wiki para Orlando, FL
    val parkIds = mapOf(
        "Magic Kingdom" to "6",
        "Animal Kingdom" to "8",
        "Disney Hollywood Studios" to "7",
        "Universal Studios" to "65",
        "Universal Island of Adventures" to "64"
        //, "Universal Epic" to "?"
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
                    // Obtenemos el ID del mapa; si no existe usamos un string vacío
                    selectedParkId = parkIds[parkName] ?: ""
                    currentScreen = Screen.Detail
                })
            }
            Screen.Detail -> {
                ParkDetailScreen(
                    parkId = selectedParkId,
                    parkName = selectedParkName
                )
            }
        }
    }
}
