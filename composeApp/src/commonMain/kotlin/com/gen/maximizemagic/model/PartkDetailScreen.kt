package com.gen.maximizemagic.model

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.MainLayout
import com.gen.maximizemagic.network.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParkDetailScreen(
    parkId: String,
    parkName: String,
    userPhotoUrl: String?, // 1. AGREGAMOS EL PARÁMETRO AQUÍ
    onBack: () -> Unit
) {
    val api = remember { ParkApi() }
    var parkData by remember { mutableStateOf<QueueTimesResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val uriHandler = LocalUriHandler.current

    // --- ESTADOS DE FILTRO Y ORDEN ---
    var searchQuery by remember { mutableStateOf("") }
    var hideClosed by remember { mutableStateOf(false) }
    var sortByWaitTime by remember { mutableStateOf(false) }

    LaunchedEffect(parkId) {
        try {
            isLoading = true
            // BIFURCACIÓN DE API: Si es Epic, usamos Themeparks.wiki
            parkData = if (parkName.contains("Epic", ignoreCase = true)) {
                api.getEpicUniverseData()
            } else {
                api.getParkData(parkId)
            }
        } finally {
            isLoading = false
        }
    }

    // 2. PASAMOS userPhotoUrl AL MainLayout
    MainLayout(
        title = parkName,
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl // <--- ESTO ACTIVA LA FOTO EN LA TOP BAR
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // --- BARRA DE BÚSQUEDA Y FILTROS ---
            Surface(tonalElevation = 2.dp, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        placeholder = { Text("Buscar atracción...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(hideClosed, { hideClosed = it }, Modifier.scale(0.8f))
                            Text("Ocultar cerradas", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(sortByWaitTime, { sortByWaitTime = it }, Modifier.scale(0.8f))
                            Text("Más espera primero", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else if (parkData == null || (parkData?.lands?.isEmpty() == true && parkData?.rides?.isEmpty() == true)) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        if (parkName.contains("Epic")) "Epic Universe abre en Mayo 2025.\n¡Pronto verás los tiempos aquí!"
                        else "No hay datos disponibles.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val allRides = (parkData!!.rides + parkData!!.lands.flatMap { it.rides }).filter {
                    it.name.contains(searchQuery, true) && (if (hideClosed) it.is_open else true)
                }.let { if (sortByWaitTime) it.sortedByDescending { r -> r.wait_time } else it }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(allRides) { RideRow(it) { /* Lógica de Google Maps aquí si deseas */ } }
                }
            }
        }
    }
}

@Composable
fun RideRow(attraction: AttractionAlternative, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(attraction.name, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            if (attraction.is_open) "${attraction.wait_time} min" else "Cerrado",
            fontWeight = FontWeight.Bold,
            color = if (attraction.is_open) Color(0xFF4CAF50) else Color.Gray
        )
    }
}

@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(Modifier.layout { m, c ->
    val p = m.measure(c)
    layout(p.width, p.height) { p.placeWithLayer(0, 0) { scaleX = scale; scaleY = scale } }
})