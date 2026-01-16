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
    // 1. Cargamos el gestor de configuración para saber el idioma
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // --- ESTADO PARA EL DIÁLOGO DE SALIDA ---
    var showExitDialog by remember { mutableStateOf(false) }

    // 2. DICCIONARIO DE TEXTOS
    val txtTitle = if (isEs) "Salir" else "Exit"
    val txtHeader = if (isEs) "Planifica tu Visita" else "Plan your Visit"
    val txtSelect = if (isEs) "Seleccione un Parque" else "Select a Park"
    val txtHours = if (isEs) "🕒 Horarios de Hoy" else "🕒 Today's Hours"
    val txtOpen = if (isEs) "Apertura" else "Open"
    val txtClose = if (isEs) "Cierre" else "Close"
    val txtMaps = if (isEs) "🚗 Ir a la Toll Plaza (Mapa)" else "🚗 Go to Toll Plaza (Map)"
    val txtWaitTimes = if (isEs) "🎢 Ver Tiempos de Espera" else "🎢 View Wait Times"
    val txtSettings = if (isEs) "Configuración" else "Settings"
    val txtLoadingWeather = if (isEs) "Cargando clima..." else "Loading weather..."
    val txtRain = if (isEs) "Lluvia probable" else "Rain likely"
    val txtClear = if (isEs) "Despejado" else "Clear"

    // Textos específicos del diálogo de salida
    val txtExitQuestion = if (isEs) "¿Desea salir?" else "Do you want to exit?"
    val txtConfirm = if (isEs) "Sí" else "Yes"
    val txtCancel = if (isEs) "No" else "No"

    var expanded by remember { mutableStateOf(false) }
    var selectedParkName by remember { mutableStateOf(txtSelect) }
    val selectedInfo = parksMap[selectedParkName]
    val uriHandler = LocalUriHandler.current

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

    MainLayout(
        title = txtTitle,
        showBackButton = true,
        // Al hacer click, en lugar de volver, mostramos el diálogo
        onBackClick = { showExitDialog = true },
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // --- BARRA DE CLIMA DETALLADA ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLoadingWeather) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(txtLoadingWeather, style = MaterialTheme.typography.labelLarge)
                    } else if (weather != null) {
                        val info = weather!!
                        val rainIcon = if (info.rainChance > 30) "🌧️" else "☀️"
                        val rainText = if (info.rainChance > 30) txtRain else txtClear

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🌡️ ${info.currentTemp}°C - $rainText $rainIcon",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mín: ${info.minTemp}°C | Máx: ${info.maxTemp}°C - ${info.conditionText}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    } else {
                        Text("⚠️", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = txtHeader,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

                Box(Modifier.fillMaxWidth()) {
                    OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedParkName)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        parksMap.keys.forEach { name ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedParkName = name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                selectedInfo?.let { info ->
                    Spacer(Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(txtHours, fontWeight = FontWeight.Bold)
                            Text("$txtOpen: ${info.openingHours} | $txtClose: ${info.closingHours}")
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${info.tollPlazaCoords}") },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(txtMaps) }

                    OutlinedButton(
                        onClick = { onNavigateToDetail(selectedParkName, info) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(txtWaitTimes) }
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- BOTÓN DE CONFIGURACIÓN AMPLIADO ---
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToSettings() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = txtSettings,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN DE SALIDA ---
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(text = txtTitle, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = txtExitQuestion)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onBack() // Navega hacia atrás
                    }
                ) {
                    Text(txtConfirm)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text(txtCancel)
                }
            }
        )
    }
}