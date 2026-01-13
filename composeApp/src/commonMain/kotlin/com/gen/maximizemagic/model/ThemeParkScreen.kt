package com.gen.maximizemagic.model

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gen.maximizemagic.MainLayout
import com.gen.maximizemagic.ParkInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeParksScreen(
    parksMap: Map<String, ParkInfo>,
    onNavigateToDetail: (String, ParkInfo) -> Unit,
    onBack: () -> Unit // <--- AGREGADO: Parámetro para la acción de volver
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedParkName by remember { mutableStateOf("Seleccione un Parque") }
    val selectedInfo = parksMap[selectedParkName]

    // El UriHandler permite abrir Google Maps en el celular
    val uriHandler = LocalUriHandler.current

    // Pasamos onBackClick = onBack para que la flecha de la barra superior funcione
    MainLayout(
        title = "Seleccionar Parque",
        showBackButton = true,
        onBackClick = onBack // <--- AGREGADO: Conexión con el Layout
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Planifica tu día mágico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- MENÚ DESPLEGABLE ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedParkName)
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

            // --- INFORMACIÓN DINÁMICA ---
            if (selectedInfo != null) {
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🕒 Horarios de Hoy", fontWeight = FontWeight.Bold)
                        Text("Apertura: ${selectedInfo.openingHours} AM")
                        Text("Cierre: ${selectedInfo.closingHours} PM")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN MAPA
                Button(
                    onClick = {
                        val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${selectedInfo.tollPlazaCoords}"
                        uriHandler.openUri(mapsUrl)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("🚗 Ir a la Toll Plaza (Peaje)")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOTÓN TIEMPOS DE ESPERA
                OutlinedButton(
                    onClick = { onNavigateToDetail(selectedParkName, selectedInfo) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎢 Ver Tiempos de Espera")
                }
            }
        }
    }
}