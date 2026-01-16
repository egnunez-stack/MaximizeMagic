package com.gen.maximizemagic.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
    var selectedDateText by remember { mutableStateOf("") }

    // Lista de parques (puedes pasarla por parámetro o tenerla aquí)
    val parksList = listOf(
        "Magic Kingdom", "Animal Kingdom", "Disney Hollywood Studios",
        "Epcot", "Universal Studios Florida", "Islands of Adventure", "Universal Epic Universe"
    )

    // Formatear fecha seleccionada
    val millis = datePickerState.selectedDateMillis
    val dateString = if (millis != null) {
        val instant = Instant.fromEpochMilliseconds(millis)
        val date = instant.toLocalDateTime(TimeZone.UTC).date
        date.toString() // YYYY-MM-DD
    } else ""

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
            Text(
                text = if (isEs) "1. Selecciona un día" else "1. Select a day",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // CALENDARIO (DatePicker)
            DatePicker(
                state = datePickerState,
                modifier = Modifier.weight(1f),
                title = null,
                headline = null,
                showModeToggle = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN PARA SELECCIONAR PARQUE
            Button(
                onClick = {
                    if (dateString.isNotEmpty()) {
                        selectedDateText = dateString
                        showParkSelector = true
                    }
                },
                enabled = dateString.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isEs) "2. Elegir Parque para $dateString" else "2. Choose Park for $dateString")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN CANCELAR (Vuelve atrás)
            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEs) "Cancelar" else "Cancel")
            }
        }
    }

    // --- DIÁLOGO DE SELECCIÓN DE PARQUE ---
    if (showParkSelector) {
        AlertDialog(
            onDismissRequest = { showParkSelector = false },
            title = { Text(if (isEs) "Seleccionar Parque" else "Select Park") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    parksList.forEach { park ->
                        ListItem(
                            headlineContent = { Text(park) },
                            modifier = Modifier.clickable {
                                // GUARDAR LÓGICA: "fecha:parque"
                                val currentAgenda = settingsManager.parkAgenda
                                val newEntry = "$selectedDateText:$park"

                                // Evitar duplicados de fecha (limpiar si ya existe esa fecha)
                                val filteredAgenda = currentAgenda.split("|")
                                    .filter { it.isNotEmpty() && !it.startsWith(selectedDateText) }
                                    .toMutableList()

                                filteredAgenda.add(newEntry)
                                settingsManager.parkAgenda = filteredAgenda.joinToString("|")

                                showParkSelector = false
                                onBack() // Volver tras grabar
                            }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}