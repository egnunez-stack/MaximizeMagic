package com.gen.maximizemagic.model

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gen.maximizemagic.network.*
import com.gen.maximizemagic.ui.components.MainLayout

@Composable
fun ParkDetailScreen(parkId: String, parkName: String) {
    // Inicializamos el cliente de la API
    val api = remember { ParkApi() }

    // Estados para la lista de atracciones e indicación de carga
    var attractions by remember { mutableStateOf<List<AttractionAlternative>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Efecto para cargar los datos cuando cambia el parkId o al iniciar la pantalla
    LaunchedEffect(parkId) {
        if (parkId.isEmpty()) {
            errorMessage = "ID de parque no válido"
            isLoading = false
            return@LaunchedEffect
        }

        try {
            isLoading = true
            errorMessage = null
            // Llamada a la nueva API alternativa
            val result = api.getAttractions(parkId)
            attractions = result

            if (result.isEmpty()) {
                errorMessage = "No se encontraron tiempos de espera. El parque podría estar cerrado."
            }
        } catch (e: Exception) {
            errorMessage = "Error de conexión: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    MainLayout(title = parkName, showBackButton = true) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Indicador de carga centrado
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                // Mensaje de error o parque cerrado
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                // Lista de atracciones tabulada
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Cabecera de la tabla
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Atracción",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Espera",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider()
                    }

                    // Filas de la tabla con los datos de Queue-Times
                    items(attractions) { attraction ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = attraction.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Lógica para mostrar minutos o "Cerrado"
                            val isWaitTimeHigh = attraction.wait_time > 45
                            Text(
                                text = if (attraction.is_open) "${attraction.wait_time} min" else "Cerrado",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (!attraction.is_open) Color.Gray
                                else if (isWaitTimeHigh) Color.Red
                                else Color.Unspecified,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}
