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
    userPhotoUrl: String?, // Add this line
    onNavigateToDetail: (String, ParkInfo) -> Unit,
    onBack: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedParkName by remember { mutableStateOf("Seleccione un Parque") }
    val selectedInfo = parksMap[selectedParkName]
    val uriHandler = LocalUriHandler.current

    MainLayout(title = "Explorar", showBackButton = true, onBackClick = onBack) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Planifica tu Visita", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // DROPDOWN MENU
            Box(Modifier.fillMaxWidth()) {
                OutlinedCard(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text(selectedParkName)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    parksMap.keys.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = { selectedParkName = name; expanded = false }
                        )
                    }
                }
            }

            // INFO DINÁMICA
            selectedInfo?.let { info ->
                Spacer(Modifier.height(32.dp))
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🕒 Horarios", fontWeight = FontWeight.Bold)
                        Text("Apertura: ${info.openingHours} | Cierre: ${info.closingHours}")
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${info.tollPlazaCoords}") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🚗 Ir a la Toll Plaza (Mapa)") }

                OutlinedButton(
                    onClick = { onNavigateToDetail(selectedParkName, info) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🎢 Ver Tiempos de Espera") }
            }
        }
    }
}