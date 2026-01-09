package com.gen.maximizemagic.model

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.MainLayout
import com.gen.maximizemagic.network.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParkDetailScreen(parkId: String, parkName: String, onBack: () -> Unit) {
    val api = remember { ParkApi() }
    var parkData by remember { mutableStateOf<QueueTimesResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // --- ESTADOS DE FILTRO ---
    var searchQuery by remember { mutableStateOf("") }
    var hideClosed by remember { mutableStateOf(false) }

    LaunchedEffect(parkId) {
        try {
            isLoading = true
            parkData = api.getParkData(parkId)
        } finally {
            isLoading = false
        }
    }

    MainLayout(title = parkName, showBackButton = true, onBackClick = onBack) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // --- 1. BARRA DE BÚSQUEDA Y FILTROS ---
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por atracción o área...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = hideClosed,
                            onCheckedChange = { hideClosed = it }
                        )
                        Text(
                            text = "Ocultar atracciones cerradas",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (parkData == null || (parkData?.lands?.isEmpty() == true && parkData?.rides?.isEmpty() == true)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏜️", fontSize = 50.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "El parque está cerrado actualmente o los datos no están disponibles.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                // --- PROCESAMIENTO DE FILTROS ---
                val allFilteredRides = (parkData!!.rides + parkData!!.lands.flatMap { it.rides })
                    .filter { ride ->
                        val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true)
                        val matchesOpen = if (hideClosed) ride.is_open else true
                        matchesSearch && matchesOpen
                    }

                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    // --- 2. SECCIÓN: RECOMENDACIÓN MÁGICA (Solo si no hay búsqueda activa) ---
                    if (searchQuery.isEmpty()) {
                        val recommendation = allFilteredRides
                            .filter { it.is_open && it.wait_time > 0 }
                            .minByOrNull { it.wait_time }

                        if (recommendation != null) {
                            item {
                                Card(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("✨ Recomendación Mágica", fontWeight = FontWeight.Bold, color = Color(0xFFFBC02D))
                                        Text("¡Ve a ${recommendation.name} ahora!", style = MaterialTheme.typography.titleMedium)
                                        Text("¡Solo ${recommendation.wait_time} min de espera!", fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }

                    // --- 3. SECCIÓN: LISTADO FILTRADO POR ÁREAS ---
                    parkData!!.lands.forEach { land ->
                        // Filtramos las rides de este land específico
                        val filteredRidesInLand = land.rides.filter { ride ->
                            val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true) ||
                                    land.name.contains(searchQuery, ignoreCase = true)
                            val matchesOpen = if (hideClosed) ride.is_open else true
                            matchesSearch && matchesOpen
                        }

                        if (filteredRidesInLand.isNotEmpty()) {
                            stickyHeader {
                                Text(
                                    text = land.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            items(filteredRidesInLand) { attraction ->
                                RideRow(attraction)
                                HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }

                    // Atracciones sueltas filtradas
                    val filteredStandaloneRides = parkData!!.rides.filter { ride ->
                        val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true)
                        val matchesOpen = if (hideClosed) ride.is_open else true
                        matchesSearch && matchesOpen
                    }

                    if (filteredStandaloneRides.isNotEmpty()) {
                        item {
                            Text(
                                "Otras Atracciones",
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        items(filteredStandaloneRides) { RideRow(it) }
                    }

                    // Mensaje si la búsqueda no arroja resultados
                    if (allFilteredRides.isEmpty()) {
                        item {
                            Text(
                                "No se encontraron atracciones que coincidan con tu búsqueda.",
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RideRow(attraction: AttractionAlternative) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = attraction.name, style = MaterialTheme.typography.bodyLarge)
            if (attraction.last_updated.isNotEmpty()) {
                val time = attraction.last_updated.take(16).replace("T", " ")
                Text(text = "Actualizado: $time", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        val isWaitTimeHigh = attraction.wait_time > 45
        Text(
            text = if (attraction.is_open) "${attraction.wait_time} min" else "Cerrado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                !attraction.is_open -> Color.Gray
                isWaitTimeHigh -> Color.Red
                attraction.wait_time < 15 -> Color(0xFF4CAF50)
                else -> Color.Unspecified
            }
        )
    }
}
