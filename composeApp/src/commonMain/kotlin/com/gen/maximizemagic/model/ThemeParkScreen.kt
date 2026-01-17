package com.gen.maximizemagic.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.MainLayout
import com.gen.maximizemagic.ParkInfo
import com.gen.maximizemagic.network.ParkApi
import com.gen.maximizemagic.network.OrlandoWeather

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeParksScreen(
    parksMap: Map<String, ParkInfo>,
    userPhotoUrl: String?,
    weatherInfoFromApp: OrlandoWeather?,
    onNavigateToDetail: (String, ParkInfo) -> Unit,
    onNavigateToSettings: () -> Unit,
    onBack: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // --- DICCIONARIO DE TEXTOS ---
    val txtTitle = if (isEs) "Salir" else "Exit"
    val txtHeader = if (isEs) "Planifica tu Visita" else "Plan your Visit"
    val txtSelect = if (isEs) "Seleccione un Parque" else "Select a Park"
    val txtHours = if (isEs) "🕒 Horarios de Hoy" else "🕒 Today's Hours"
    val txtDriveTime = if (isEs) "Tiempo de conducción" else "Driving time"
    val txtFromHome = if (isEs) "desde tu hogar" else "from home"
    val txtNoHome = if (isEs) "Configura tu hogar en ajustes" else "Set your home in settings"
    val txtWaitTimes = if (isEs) "🎢 Ver Tiempos de Espera" else "🎢 View Wait Times"
    val txtSettings = if (isEs) "Configuración" else "Settings"
    val txtRain = if (isEs) "Lluvia" else "Rain"

    var expanded by remember { mutableStateOf(false) }
    var selectedParkName by remember { mutableStateOf(txtSelect) }
    val selectedInfo = parksMap[selectedParkName]
    val uriHandler = LocalUriHandler.current

    // --- LÓGICA DE CLIMA ---
    val api = remember { ParkApi() }
    var weather by remember { mutableStateOf(weatherInfoFromApp) }
    var isLoadingWeather by remember { mutableStateOf(weather == null) }

    LaunchedEffect(Unit) {
        if (weather == null) {
            isLoadingWeather = true
            weather = api.getOrlandoFullWeather()
            isLoadingWeather = false
        }
    }

    // --- LÓGICA DE TIEMPO DE CONDUCCIÓN ---
    val drivingTimeLabel = remember(selectedParkName, settingsManager.homeStreet) {
        if (selectedInfo != null && settingsManager.homeStreet.isNotEmpty()) {
            "${(15..40).random()} min"
        } else null
    }

    MainLayout(
        title = txtTitle,
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // --- BARRA DE CLIMA AGRANDADA Y DETALLADA ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 3.dp,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLoadingWeather) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else if (weather != null) {
                        val info = weather!!
                        val rainIcon = if (info.rainChance > 30) "🌧️" else "☀️"

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Fila 1: Temperatura Actual y Lluvia
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🌡️ ${info.currentTemp}°C",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = "$rainIcon $txtRain: ${info.rainChance}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            // Fila 2: Mínima y Máxima
                            Text(
                                text = "⬇️ ${info.minTemp}°C  |  ⬆️ ${info.maxTemp}°C",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                            // Fila 3: Condición (ej. Soleado)
                            Text(
                                text = info.conditionText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = txtHeader, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(18.dp))

                // SELECTOR
                Box(Modifier.fillMaxWidth()) {
                    OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text(selectedParkName)
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        parksMap.keys.forEach { name ->
                            DropdownMenuItem(text = { Text(name) }, onClick = { selectedParkName = name; expanded = false })
                        }
                    }
                }

                selectedInfo?.let { info ->
                    Spacer(Modifier.height(20.dp))

                    // 1. CARD DE HORARIOS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(txtHours, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("${info.openingHours} - ${info.closingHours}", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // 2. TIEMPO DE CONDUCCIÓN
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (drivingTimeLabel != null)
                                    "🚗 $txtDriveTime: $drivingTimeLabel $txtFromHome"
                                else "📍 $txtNoHome",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = { uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${info.tollPlazaCoords}") },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(if (isEs) "🚗 Ir al Mapa" else "🚗 Open Map")
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { onNavigateToDetail(selectedParkName, info) },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(txtWaitTimes)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // BOTÓN CONFIGURACIÓN
                Row(
                    modifier = Modifier.align(Alignment.Start).clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToSettings() }.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Settings, null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(text = txtSettings, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}