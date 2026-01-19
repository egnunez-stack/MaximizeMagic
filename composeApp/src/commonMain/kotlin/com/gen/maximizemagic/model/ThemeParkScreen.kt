package com.gen.maximizemagic.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.ParkInfo
import com.gen.maximizemagic.network.ParkApi
import com.gen.maximizemagic.network.OrlandoWeather
import com.gen.maximizemagic.ui.layout.MainLayout
import kotlinx.datetime.*

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
    val uriHandler = LocalUriHandler.current

    // Color Dorado (Igual al título)
    val magicGold = Color(0xFFD4AF37)

    // --- DICCIONARIO DE TEXTOS ---
    val txtExit = if (isEs) "Salir" else "Exit"
    val txtHeader = if (isEs) "Planifica tu Visita" else "Plan your Visit"
    val txtSelect = if (isEs) "Seleccione un Parque" else "Select a Park"
    val txtDriveTime = if (isEs) "Tiempo de conducción" else "Driving time"
    val txtFromHome = if (isEs) "desde tu hogar" else "from home"
    val txtNoHome = if (isEs) "Configura tu hogar en ajustes" else "Set your home in settings"
    val txtSettings = if (isEs) "Configuración" else "Settings"
    val txtHelp = if (isEs) "Ayuda" else "Help"
    val txtOpen = if (isEs) "Abre:" else "Open:"
    val txtClose = if (isEs) "Cierra:" else "Close:"
    val txtRain = if (isEs) "Lluvia:" else "Rain:"

    var selectedParkName by remember { mutableStateOf(txtSelect) }
    val selectedInfo = parksMap[selectedParkName]

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

    // --- TIEMPO DE CONDUCCIÓN SIMULADO ---
    val drivingTimeLabel = remember(selectedParkName, settingsManager.homeStreet) {
        if (selectedInfo != null && settingsManager.homeStreet.isNotEmpty()) "${(15..40).random()} min" else null
    }

    MainLayout(
        title = txtExit,
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TÍTULO PRINCIPAL (Dorado)
            Text(
                text = txtHeader,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 35.sp,
                    color = magicGold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(16.dp))

            // 2. BARRA DE CLIMA
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoadingWeather) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else if (weather != null) {
                        val w = weather!!
                        Text("📍 Orlando, FL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌡️ ${w.currentTemp}°C",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("🌧️ $txtRain ${w.rainChance}%", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = "⬇️ ${w.minTemp}°C | ⬆️ ${w.maxTemp}°C | ${w.conditionText}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 3. SELECTOR DE PARQUES (Color igual a los botones y Font Magic)
            var expanded by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxWidth()) {
                Card(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        // TEXTO SELECCIONADO CON FUENTE MAGIC
                        Text(
                            text = selectedParkName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    parksMap.keys.forEach { name ->
                        DropdownMenuItem(
                            text = {
                                // ITEMS DE LA LISTA CON FUENTE MAGIC
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                selectedParkName = name
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 4. BOTONES CENTRALES
            if (selectedInfo != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // BOTÓN 1: HORARIOS / TIEMPOS DE ESPERA
                    Button(
                        onClick = { onNavigateToDetail(selectedParkName, selectedInfo) },
                        modifier = Modifier.fillMaxWidth().height(70.dp), // Más alto para que entren bien las letras
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$txtOpen ${selectedInfo.openingHours} - $txtClose ${selectedInfo.closingHours}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (isEs) "Ver Tiempos de Espera" else "View Wait Times",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // BOTÓN 2: TIEMPO DE CONDUCCIÓN
                    Button(
                        onClick = {
                            if (settingsManager.homeStreet.isNotEmpty()) {
                                val home = "${settingsManager.homeStreet} ${settingsManager.homeNumber}, ${settingsManager.homeCity}"
                                val url = "https://www.google.com/maps/dir/?api=1&origin=$home&destination=${selectedInfo.tollPlazaCoords}&travelmode=driving"
                                uriHandler.openUri(url.replace(" ", "+"))
                            } else {
                                onNavigateToSettings()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(70.dp), // Más alto para que entren bien las letras
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    ) {
                        val driveText = if (drivingTimeLabel != null)
                            "🚗 $txtDriveTime: $drivingTimeLabel $txtFromHome"
                        else "📍 $txtNoHome"

                        Text(
                            text = driveText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. PARTE INFERIOR: CONFIGURACIÓN Y AYUDA
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToSettings() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = magicGold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = txtSettings,
                        color = magicGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                FloatingActionButton(
                    onClick = { /* Ayuda */ },
                    modifier = Modifier.align(Alignment.CenterEnd).size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Info, contentDescription = txtHelp)
                }
            }
        }
    }
}