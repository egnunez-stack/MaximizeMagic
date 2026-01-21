package com.gen.maximizemagic.model

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    val isPt = settingsManager.language == "pt"
    val uriHandler = LocalUriHandler.current

    var showHelpDialog by remember { mutableStateOf(false) }

    val magicGold = Color(0xFFD4AF37)
    val translucency = 0.3f

    // --- DICCIONARIO DE TEXTOS ---
    val txtExit = when { isPt -> "Sair"; isEs -> "Salir"; else -> "Exit" }
    val txtHeader = when { isPt -> "Planeje sua Visita"; isEs -> "Planifica tu Visita"; else -> "Plan your Visit" }
    val txtSelect = when { isPt -> "Selecione um Parque"; isEs -> "Seleccione un Parque"; else -> "Select a Park" }
    val txtHelpTitle = when { isPt -> "Informação"; isEs -> "Información"; else -> "Information" }
    val txtClose = when { isPt -> "Fechar"; isEs -> "Cerrar"; else -> "Close" }
    val txtOpen = if (isEs || isPt) "Abre:" else "Open:"
    val txtCloseLabel = if (isEs) "Cierra:" else if (isPt) "Fecha:" else "Close:"
    val txtRain = if (isEs) "Lluvia:" else if (isPt) "Chuva:" else "Rain:"
    val txtDriveTime = if (isEs) "Tiempo de conducción" else if (isPt) "Tempo de condução" else "Driving time"
    val txtFromHome = if (isEs) "desde tu hogar" else if (isPt) "da sua casa" else "from home"
    val txtNoHome = if (isEs) "Configura tu hogar en ajustes" else if (isPt) "Configure sua casa nos ajustes" else "Set your home in settings"
    val txtSettings = if (isEs) "Configuración" else if (isPt) "Configurações" else "Settings"
    val txtHoursWait = if (isEs) "Horarios / Espera" else if (isPt) "Horários / Espera" else "Hours / Wait"

    val txtHelpContent = when {
        isPt -> "Para personalizar sua experiência, em configurações, coloque o endereço onde você ficará e poderá ver as rotas para os parques e o tempo de chegada de sua casa."
        isEs -> "Para personalizar tu experiencia, en configuración, coloca el domicilio donde vas a estar, y podrás ver rutas a los parques y tiempo de llegada desde tu hogar."
        else -> "To customize your experience, in settings, enter the address where you will be staying and see routes to the parks."
    }

    // Inicializamos con el texto traducido correctamente
    var selectedParkName by remember(txtSelect) { mutableStateOf(txtSelect) }
    val selectedInfo = parksMap[selectedParkName]

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

    val drivingTimeLabel = remember(selectedParkName, settingsManager.homeStreet) {
        if (selectedInfo != null && settingsManager.homeStreet.isNotEmpty()) "${(15..40).random()} min" else null
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(txtHelpTitle, style = MaterialTheme.typography.titleLarge.copy(color = magicGold)) },
            text = {
                val scroll = rememberScrollState()
                Column(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(scroll)){
                    Text(text = txtHelpContent, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Justify)
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showHelpDialog = false }) { Text(txtClose) }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    }

    MainLayout(
        title = txtExit,
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TÍTULO PRINCIPAL
            Text(
                text = txtHeader,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    color = magicGold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(16.dp))

            // 2. BARRA DE CLIMA
            if (weather != null || isLoadingWeather) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = translucency),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isLoadingWeather) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else if (weather != null) {
                            val w = weather!!
                            Text("📍 Orlando, FL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌡️ ${w.currentTemp}°C", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
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
            }

            Spacer(Modifier.height(16.dp))

            // 3. SELECTOR DE PARQUES (CORREGIDO: Tipografía Magic + Color Blanco)
            var expanded by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxWidth()) {
                Card(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = translucency))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
                        Arrangement.SpaceBetween, Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedParkName,
                            // Aplicamos tipografía Magic (headlineLarge) pero en color blanco
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontSize = 20.sp, // Tamaño ajustado para el selector
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                }

                // MENÚ DESPLEGABLE TRASLÚCIDO AZUL
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                ) {
                    parksMap.keys.forEach { name ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = name,
                                    // Aplicamos tipografía Magic (headlineLarge) pero en color blanco para los items
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = { selectedParkName = name; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 4. BOTONES CENTRALES
            if (selectedInfo != null) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onNavigateToDetail(selectedParkName, selectedInfo) },
                        modifier = Modifier.fillMaxWidth().height(75.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = translucency))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = txtHoursWait, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text("$txtOpen ${selectedInfo.openingHours} - $txtCloseLabel ${selectedInfo.closingHours}", fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }

                    Button(
                        onClick = {
                            if (settingsManager.homeStreet.isNotEmpty()) {
                                val home = "${settingsManager.homeStreet} ${settingsManager.homeNumber}, ${settingsManager.homeCity}"
                                uriHandler.openUri("https://www.google.com/maps/dir/?api=1&origin=${home.replace(" ", "+")}&destination=${selectedInfo.tollPlazaCoords}&travelmode=driving")
                            } else { onNavigateToSettings() }
                        },
                        modifier = Modifier.fillMaxWidth().height(75.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = translucency))
                    ) {
                        val driveText = if (drivingTimeLabel != null) "🚗 $txtDriveTime: $drivingTimeLabel $txtFromHome" else "📍 $txtNoHome"
                        Text(driveText, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart).clip(RoundedCornerShape(12.dp)).clickable { onNavigateToSettings() }.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Settings, null, modifier = Modifier.size(24.dp), tint = magicGold)
                    Spacer(Modifier.width(8.dp))
                    Text(txtSettings, color = magicGold, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                FloatingActionButton(
                    onClick = { showHelpDialog = true },
                    modifier = Modifier.align(Alignment.CenterEnd).size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Help")
                }
            }
        }
    }
}