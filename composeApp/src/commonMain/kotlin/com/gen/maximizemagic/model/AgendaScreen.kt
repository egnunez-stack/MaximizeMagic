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

    // --- CORRECCIÓN DE FECHA SELECCIONADA ---
    // Usamos TimeZone.UTC para extraer el día que el usuario ve en el calendario
    val dateString = remember(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            val instant = Instant.fromEpochMilliseconds(millis)
            val date = instant.toLocalDateTime(TimeZone.UTC).date
            date.toString()
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

                // ... (dentro del onClick del botón Grabar) ...
                Button(
                    onClick = {
                        settingsManager.parkAgenda = tempAgenda

                        if (dateString.isNotEmpty()) {
                            val entryToday = tempAgenda.split("|").find { it.startsWith(dateString) }

                            if (entryToday != null) {
                                val pName = entryToday.substringAfter(":")
                                val pOpen = parksInfo[pName] ?: "09:00"

                                try {
                                    // 1. Extraer componentes de fecha y hora
                                    val hour = pOpen.substringBefore(":").toInt()
                                    val min = pOpen.substringAfter(":").toInt()
                                    val parkDate = LocalDate.parse(dateString)

                                    // 2. Construir LocalDateTime manual para evitar confusiones de zona horaria
                                    val openingDateTime = LocalDateTime(
                                        year = parkDate.year,
                                        monthNumber = parkDate.monthNumber,
                                        dayOfMonth = parkDate.dayOfMonth,
                                        hour = hour,
                                        minute = min
                                    )

                                    // 3. Convertir a instante real usando el Reloj del Sistema
                                    val systemTz = TimeZone.currentSystemDefault()
                                    val openingInstant = openingDateTime.toInstant(systemTz)

                                    // 4. Restar 80 minutos exactos
                                    val leadTimeMillis = 80L * 60L * 1000L
                                    val alarmTimeMillis = openingInstant.toEpochMilliseconds() - leadTimeMillis

                                    val msg = if (isEs) "¡Despierta! Rumbo a $pName" else "Wake up! Heading to $pName"

                                    // LOG de depuración para Logcat
                                    println("#MaximizeMagic: Programando alarma para el día real: ${parkDate} (Milis: $alarmTimeMillis)")

                                    // 5. Llamar al manager
                                    alarmManager.setAlarm(alarmTimeMillis, msg)

                                } catch (e: Exception) {
                                    println("#MaximizeMagic: Error en cálculo: ${e.message}")
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