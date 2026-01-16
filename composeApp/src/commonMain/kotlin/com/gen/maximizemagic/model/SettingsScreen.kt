package com.gen.maximizemagic.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.MainLayout
import kotlinx.coroutines.delay
import kotlinx.datetime.* // Librería oficial KMP para fechas

@Composable
fun SettingsScreen(
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    var currentLanguage by remember { mutableStateOf(settingsManager.language) }
    val isEs = currentLanguage == "es"

    // Estados para Diálogos
    var showHomeDialog by remember { mutableStateOf(false) }
    var showArrivalDialog by remember { mutableStateOf(false) }
    var showDepartureDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Estado para el Aviso Temporal
    var checkInNotice by remember { mutableStateOf<String?>(null) }

    // Diccionario de textos
    val texts = remember(currentLanguage) {
        if (isEs) mapOf(
            "title" to "Configuración",
            "header" to "Configuración Personal",
            "home" to "Configurar Hogar",
            "arrival" to "Vuelo de Ida",
            "departure" to "Vuelo de Vuelta",
            "lang" to "Idioma",
            "save" to "Guardar",
            "cancel" to "Cancelar",
            "street" to "Calle",
            "number" to "Altura",
            "city" to "Ciudad",
            "select_lang" to "Seleccionar Idioma",
            "flight_num" to "Nro de Vuelo",
            "date" to "Fecha (AAAA-MM-DD)",
            "time" to "Hora (HH:MM)",
            "checkin_msg" to "¡Atención! Ya puedes hacer el Check-in del vuelo: "
        ) else mapOf(
            "title" to "Settings",
            "header" to "Personal Settings",
            "home" to "Configure Home",
            "arrival" to "Arrival Flight",
            "departure" to "Departure Flight",
            "lang" to "Language",
            "save" to "Save",
            "cancel" to "Cancel",
            "street" to "Street",
            "number" to "Number",
            "city" to "City",
            "select_lang" to "Select Language",
            "flight_num" to "Flight Number",
            "date" to "Date (YYYY-MM-DD)",
            "time" to "Time (HH:MM)",
            "checkin_msg" to "Attention! Check-in is now open for flight: "
        )
    }

    // Lógica para desaparecer el aviso tras 7 segundos
    LaunchedEffect(checkInNotice) {
        if (checkInNotice != null) {
            delay(7000)
            checkInNotice = null
        }
    }

    // Función interna para validar check-in (Lógica KMP corregida)
    fun validateCheckIn(date: String, time: String, flight: String) {
        try {
            val now = Clock.System.now()
            // Combinamos fecha y hora en formato ISO: 2024-12-31T15:30:00
            val flightDateTime = LocalDateTime.parse("${date}T${time}:00")
            val flightInstant = flightDateTime.toInstant(TimeZone.currentSystemDefault())

            val duration = flightInstant - now

            // Si faltan entre 0 y 24 horas para el vuelo
            if (duration.inWholeHours in 0..23) {
                checkInNotice = "${texts["checkin_msg"]}$flight"
            }
        } catch (e: Exception) {
            // Si el formato es incorrecto, no hace nada
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MainLayout(
            title = texts["title"]!!,
            showBackButton = true,
            onBackClick = onBack,
            userPhotoUrl = userPhotoUrl
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = texts["header"]!!, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                // 1. Botón Hogar
                Button(onClick = { showHomeDialog = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    val street = settingsManager.homeStreet
                    Text(if (street.isEmpty()) texts["home"]!! else "🏠 $street, ${settingsManager.homeCity}")
                }

                // 2. Botón Vuelo Ida
                Button(
                    onClick = { showArrivalDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    val f = settingsManager.arrivalFlight
                    Text(if (f.isEmpty()) "🛫 ${texts["arrival"]}" else "🛫 $f (${settingsManager.arrivalDate})")
                }

                // 3. Botón Vuelo Vuelta
                Button(
                    onClick = { showDepartureDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    val f = settingsManager.departureFlight
                    Text(if (f.isEmpty()) "🛬 ${texts["departure"]}" else "🛬 $f (${settingsManager.departureDate})")
                }

                // 4. Botón Idioma
                Button(
                    onClick = { showLanguageDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    val label = if (isEs) "Español" else "English"
                    Text("🌐 ${texts["lang"]}: $label")
                }
            }
        }

        // --- CARTEL DE AVISO TEMPORAL (7 SEGUNDOS) ---
        checkInNotice?.let { notice ->
            Card(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp, start = 20.dp, end = 20.dp),                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEB3B), contentColor = Color.Black),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Text(notice, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    // --- DIÁLOGOS DE VUELO (REUTILIZABLE) ---
    @Composable
    fun FlightDialog(isArrival: Boolean, onDismiss: () -> Unit) {
        var fNum by remember { mutableStateOf(if (isArrival) settingsManager.arrivalFlight else settingsManager.departureFlight) }
        var fDate by remember { mutableStateOf(if (isArrival) settingsManager.arrivalDate else settingsManager.departureDate) }
        var fTime by remember { mutableStateOf(if (isArrival) settingsManager.arrivalTime else settingsManager.departureTime) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(if (isArrival) texts["arrival"]!! else texts["departure"]!!, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fNum, onValueChange = { fNum = it }, label = { Text(texts["flight_num"]!!) })
                    OutlinedTextField(value = fDate, onValueChange = { fDate = it }, label = { Text(texts["date"]!!) }, placeholder = { Text("YYYY-MM-DD") })
                    OutlinedTextField(value = fTime, onValueChange = { fTime = it }, label = { Text(texts["time"]!!) }, placeholder = { Text("HH:MM") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (isArrival) {
                        settingsManager.arrivalFlight = fNum
                        settingsManager.arrivalDate = fDate
                        settingsManager.arrivalTime = fTime
                    } else {
                        settingsManager.departureFlight = fNum
                        settingsManager.departureDate = fDate
                        settingsManager.departureTime = fTime
                    }
                    validateCheckIn(fDate, fTime, fNum)
                    onDismiss()
                }) { Text(texts["save"]!!) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(texts["cancel"]!!) }
            }
        )
    }

    if (showArrivalDialog) FlightDialog(true) { showArrivalDialog = false }
    if (showDepartureDialog) FlightDialog(false) { showDepartureDialog = false }

    // --- DIÁLOGOS HOGAR ---
    if (showHomeDialog) {
        var tempStreet by remember { mutableStateOf(settingsManager.homeStreet) }
        var tempNumber by remember { mutableStateOf(settingsManager.homeNumber) }
        var tempCity by remember { mutableStateOf(if(settingsManager.homeCity.isEmpty()) "Orlando" else settingsManager.homeCity) }
        var cityExpanded by remember { mutableStateOf(false) }
        val cities = listOf("Orlando", "Kissimmee", "Celebration", "Winter Garden", "Lake Buena Vista", "Davenport")

        AlertDialog(
            onDismissRequest = { showHomeDialog = false },
            title = { Text(texts["home"]!!, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tempStreet, onValueChange = { tempStreet = it }, label = { Text(texts["street"]!!) })
                    OutlinedTextField(value = tempNumber, onValueChange = { tempNumber = it }, label = { Text(texts["number"]!!) })
                    Box {
                        OutlinedTextField(
                            value = tempCity, onValueChange = { }, readOnly = true, label = { Text(texts["city"]!!) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { cityExpanded = true }) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                            cities.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        tempCity = c;
                                        cityExpanded = false
                                    }
                                )
                            }                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    settingsManager.homeStreet = tempStreet
                    settingsManager.homeNumber = tempNumber
                    settingsManager.homeCity = tempCity
                    showHomeDialog = false
                }) { Text(texts["save"]!!) }
            },
            dismissButton = {
                TextButton(onClick = { showHomeDialog = false }) { Text(texts["cancel"]!!) }
            }
        )
    }

    // --- DIÁLOGO IDIOMA ---
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(texts["select_lang"]!!, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    ListItem(headlineContent = { Text("Español") }, modifier = Modifier.clickable {
                        settingsManager.language = "es"; currentLanguage = "es"; showLanguageDialog = false
                    })
                    HorizontalDivider()
                    ListItem(headlineContent = { Text("English") }, modifier = Modifier.clickable {
                        settingsManager.language = "en"; currentLanguage = "en"; showLanguageDialog = false
                    })
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text(texts["cancel"]!!) }
            }
        )
    }
}