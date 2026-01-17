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
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.ui.MainLayout
import com.gen.maximizemagic.ui.layout.MainLayout
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // --- ESTADOS ---
    val datePickerState = rememberDatePickerState()
    var showParkSelector by remember { mutableStateOf(false) }

    // Copia local para persistencia temporal
    var tempAgenda by remember { mutableStateOf(settingsManager.parkAgenda) }

    val parksList = listOf(
        "Magic Kingdom", "Animal Kingdom", "Disney Hollywood Studios",
        "Epcot", "Universal Studios Florida", "Islands of Adventure", "Universal Epic Universe"
    )

    // Formatear fecha seleccionada
    val millis = datePickerState.selectedDateMillis
    val dateString = if (millis != null) {
        val instant = Instant.fromEpochMilliseconds(millis)
        val date = instant.toLocalDateTime(TimeZone.UTC).date
        date.toString()
    } else ""

    // Buscar parque guardado para la fecha
    val parkForSelectedDate = remember(dateString, tempAgenda) {
        if (dateString.isEmpty()) null
        else {
            tempAgenda.split("|")
                .find { it.startsWith(dateString) }
                ?.substringAfter(":")
        }
    }


        // ... dentro de AgendaScreen ...
        MainLayout(
            title = if (isEs) "Agenda de Parques" else "Park Schedule",
            showBackButton = true,
            onBackClick = onBack,
            userPhotoUrl = userPhotoUrl // <--- Ahora el compilador ya no dará error
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. CALENDARIO
            DatePicker(
                state = datePickerState,
                modifier = Modifier.weight(1f),
                title = null,
                headline = null,
                showModeToggle = false
            )

            // 2. INFO DEL DÍA
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

            // 3. BOTONES DE GRABAR / CANCELAR
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
                        settingsManager.parkAgenda = tempAgenda
                        onBack()
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(if (isEs) "Grabar" else "Save")
                }
            }
        }
    }

    // --- DIÁLOGO DE SELECCIÓN ---
    if (showParkSelector) {
        AlertDialog(
            onDismissRequest = { showParkSelector = false },
            title = { Text(if (isEs) "Elegir Parque" else "Choose Park") },
            text = {
                Column {
                    // --- OPCIÓN PARA BORRAR (NULO) ---
                    ListItem(
                        headlineContent = {
                            Text(
                                if (isEs) "❌ Ninguno (Borrar)" else "❌ None (Delete)",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier.clickable {
                            // Eliminamos la entrada para esta fecha
                            val list = tempAgenda.split("|")
                                .filter { it.isNotEmpty() && !it.startsWith(dateString) }
                            tempAgenda = list.joinToString("|")
                            showParkSelector = false
                        }
                    )
                    HorizontalDivider()

                    // --- LISTA DE PARQUES ---
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