package com.gen.maximizemagic.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gen.maximizemagic.ui.layout.MainLayout
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    val alarmManager = remember { AlarmManager() }
    val isEs = settingsManager.language == "es"

    val parksInfo = mapOf(
        "Magic Kingdom" to "09:00",
        "Animal Kingdom" to "08:00",
        "Disney Hollywood Studios" to "09:00",
        "Epcot" to "09:00",
        "Universal Studios Florida" to "09:00",
        "Islands of Adventure" to "09:00",
        "Universal Epic Universe" to "09:00"
    )

    val datePickerState = rememberDatePickerState()
    var showParkSelector by remember { mutableStateOf(false) }
    var tempAgenda by remember { mutableStateOf(settingsManager.parkAgenda) }

    val parksList = parksInfo.keys.toList()

    // --- CORRECCIÓN DEFINITIVA DE FECHA SELECCIONADA ---
    val dateString = remember(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            // Usamos UTC para obtener el día exacto que el usuario marcó en el calendario
            val instant = Instant.fromEpochMilliseconds(millis)
            val date = instant.toLocalDateTime(TimeZone.UTC).date
            date.toString() // Devuelve "YYYY-MM-DD"
        } else ""
    }

    val parkForSelectedDate = remember(dateString, tempAgenda) {
        if (dateString.isEmpty()) null
        else {
            tempAgenda.split("|")
                .find { it.startsWith(dateString) }
                ?.substringAfter(":")
        }
    }

    MainLayout(
        title = if (isEs) "Agenda de Parques" else "Park Schedule",
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
            DatePicker(
                state = datePickerState,
                modifier = Modifier.weight(1f),
                title = null,
                headline = null,
                showModeToggle = false
            )

            if (dateString.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(if (isEs) "Día: $dateString" else "Date: $dateString", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = if (parkForSelectedDate != null)
                                (if (isEs) "Parque: $parkForSelectedDate" else "Park: $parkForSelectedDate")
                            else (if (isEs) "Sin parque asignado" else "No park assigned"),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { showParkSelector = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(if (isEs) "Seleccionar / Cambiar Parque" else "Select / Change Park")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(if (isEs) "Cancelar" else "Cancel")
                }

                Button(
                    onClick = {
                        // 1. Guardar la agenda primero
                        settingsManager.parkAgenda = tempAgenda

                        if (dateString.isNotEmpty()) {
                            val entryToday = tempAgenda.split("|").find { it.startsWith(dateString) }

                            if (entryToday != null) {
                                val pName = entryToday.substringAfter(":")
                                val pOpen = parksInfo[pName] ?: "09:00"

                                try {
                                    // --- LÓGICA DE ALARMA MANUAL Y SEGURA ---

                                    // Separamos la hora de apertura (09:00)
                                    val hourParts = pOpen.split(":")
                                    val openHour = hourParts[0].toInt()
                                    val openMin = hourParts[1].toInt()

                                    // Parseamos la fecha (2024-01-24) que sabemos que es correcta
                                    val parkDate = LocalDate.parse(dateString)

                                    // Creamos el LocalDateTime usando los componentes exactos
                                    val openingDateTime = LocalDateTime(
                                        year = parkDate.year,
                                        monthNumber = parkDate.monthNumber,
                                        dayOfMonth = parkDate.dayOfMonth,
                                        hour = openHour,
                                        minute = openMin
                                    )

                                    // Convertimos a Instant usando la zona horaria del sistema del usuario
                                    // Esto asegura que la alarma se programe en la hora local del celular
                                    val systemTz = TimeZone.currentSystemDefault()
                                    val openingInstant = openingDateTime.toInstant(systemTz)

                                    // Restamos los 80 minutos en milisegundos
                                    val alarmTimeMillis = openingInstant.toEpochMilliseconds() - (80L * 60L * 1000L)

                                    val msg = if (isEs) "¡Despierta! Rumbo a $pName" else "Wake up! Heading to $pName"

                                    // Log de depuración para ver en la consola si los milisegundos son correctos
                                    println("#MaximizeMagic: Programando alarma para $openingDateTime menos 80 min")

                                    alarmManager.setAlarm(alarmTimeMillis, msg)

                                } catch (e: Exception) {
                                    println("Error crítico en alarma: ${e.message}")
                                }
                            }
                        }
                        onBack()
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(if (isEs) "Grabar" else "Save")
                }
            }
        }
    }

    if (showParkSelector) {
        AlertDialog(
            onDismissRequest = { showParkSelector = false },
            title = { Text(if (isEs) "Elegir Parque" else "Choose Park") },
            text = {
                Column {
                    ListItem(
                        headlineContent = {
                            Text(
                                if (isEs) "❌ Ninguno (Borrar)" else "❌ None (Delete)",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable {
                            val list = tempAgenda.split("|")
                                .filter { it.isNotEmpty() && !it.startsWith(dateString) }
                            tempAgenda = list.joinToString("|")
                            showParkSelector = false
                        }
                    )
                    HorizontalDivider()
                    parksList.forEach { park ->
                        ListItem(
                            headlineContent = { Text(park) },
                            modifier = Modifier.clickable {
                                val list = tempAgenda.split("|")
                                    .filter { it.isNotEmpty() && !it.startsWith(dateString) }
                                    .toMutableList()
                                list.add("$dateString:$park")
                                tempAgenda = list.joinToString("|")
                                showParkSelector = false
                            }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}