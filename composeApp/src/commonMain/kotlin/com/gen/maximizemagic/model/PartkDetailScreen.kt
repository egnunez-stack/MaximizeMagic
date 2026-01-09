package com.gen.maximizemagic.model

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.MainLayout
import com.gen.maximizemagic.network.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParkDetailScreen(parkId: String, parkName: String, onBack: () -> Unit) {
    val api = remember { ParkApi() }
    var parkData by remember { mutableStateOf<QueueTimesResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(parkId) {
        try {
            isLoading = true
            parkData = api.getParkData(parkId)
        } finally {
            isLoading = false
        }
    }

    MainLayout(title = parkName, showBackButton = true, onBackClick = onBack) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (parkData == null || (parkData?.lands?.isEmpty() == true && parkData?.rides?.isEmpty() == true)) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
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
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // --- 1. SECCIÓN: RECOMENDACIÓN MÁGICA ---
                val allRides = (parkData!!.rides + parkData!!.lands.flatMap { it.rides })
                val recommendation = allRides
                    .filter { it.is_open && it.wait_time > 0 }
                    .minByOrNull { it.wait_time }

                if (recommendation != null) {
                    item {
                        Card(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Amarillo suave
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

                // --- 2. SECCIÓN: LISTADO POR ÁREAS (LANDS) ---
                parkData!!.lands.forEach { land ->
                    if (land.rides.isNotEmpty()) {
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

                        items(land.rides) { attraction ->
                            RideRow(attraction)
                            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }

                // Si hay atracciones sueltas (fuera de un área específica)
                if (parkData!!.rides.isNotEmpty()) {
                    item {
                        Text(
                            "Otras Atracciones",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    items(parkData!!.rides) { RideRow(it) }
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
                // Formateamos ligeramente la fecha para que sea legible
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
                attraction.wait_time < 15 -> Color(0xFF4CAF50) // Verde para esperas cortas
                else -> Color.Unspecified
            }
        )
    }
}
