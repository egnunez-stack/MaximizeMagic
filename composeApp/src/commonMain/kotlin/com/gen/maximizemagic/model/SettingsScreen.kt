package com.gen.maximizemagic.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.MainLayout

@Composable
fun SettingsScreen(
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }

    // 1. Cargamos el idioma inicial guardado
    var currentLanguage by remember { mutableStateOf(settingsManager.language) }

    // 2. DICCIONARIO REACTIVO (Se reconstruye al cambiar currentLanguage)
    val texts = remember(currentLanguage) {
        if (currentLanguage == "es") {
            mapOf(
                "title" to "Configuración",
                "header" to "Configuración Personal",
                "home" to "Configurar Hogar",
                "arrival" to "Configurar Vuelo de Llegada",
                "departure" to "Configurar Vuelo de Partida",
                "lang" to "Configurar Idioma",
                "footer" to "Los datos se guardan localmente para calcular tus traslados.",
                "flight_label" to "Vuelo",
                "lang_label" to "Idioma",
                "select_lang" to "Seleccionar Idioma"
            )
        } else {
            mapOf(
                "title" to "Settings",
                "header" to "Personal Settings",
                "home" to "Configure Home",
                "arrival" to "Configure Arrival Flight",
                "departure" to "Configure Departure Flight",
                "lang" to "Configure Language",
                "footer" to "Data is saved locally to calculate your transfers.",
                "flight_label" to "Flight",
                "lang_label" to "Language",
                "select_lang" to "Select Language"
            )
        }
    }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showHomeDialog by remember { mutableStateOf(false) }
    var showArrivalDialog by remember { mutableStateOf(false) }
    var showDepartureDialog by remember { mutableStateOf(false) }

    MainLayout(
        title = texts["title"] ?: "Settings",
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = texts["header"] ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 1. Botón Hogar
            Button(
                onClick = { showHomeDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                val address = settingsManager.homeAddress
                Text(if (address.isEmpty()) "🏠 ${texts["home"]}" else "🏠 $address")
            }

            // 2. Botón Vuelo Llegada
            Button(
                onClick = { showArrivalDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                val flight = settingsManager.arrivalFlight
                Text(if (flight.isEmpty()) "🛬 ${texts["arrival"]}" else "🛬 ${texts["flight_label"]}: $flight")
            }

            // 3. Botón Vuelo Partida
            Button(
                onClick = { showDepartureDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                val flight = settingsManager.departureFlight
                Text(if (flight.isEmpty()) "🛫 ${texts["departure"]}" else "🛫 ${texts["flight_label"]}: $flight")
            }

            // 4. Botón Idioma
            Button(
                onClick = { showLanguageDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                val langDisplay = if (currentLanguage == "es") "Español" else "English"
                Text("🌐 ${texts["lang_label"]}: $langDisplay")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = texts["footer"] ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // --- DIÁLOGO DE SELECCIÓN DE IDIOMA ---
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(texts["select_lang"] ?: "") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("🇪🇸 Español") },
                        modifier = Modifier.clickable {
                            settingsManager.language = "es"
                            currentLanguage = "es" // DISPARA RECOMPOSICIÓN
                            showLanguageDialog = false
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("🇺🇸 English") },
                        modifier = Modifier.clickable {
                            settingsManager.language = "en"
                            currentLanguage = "en" // DISPARA RECOMPOSICIÓN
                            showLanguageDialog = false
                        }
                    )
                }
            },
            confirmButton = {}
        )
    }
}