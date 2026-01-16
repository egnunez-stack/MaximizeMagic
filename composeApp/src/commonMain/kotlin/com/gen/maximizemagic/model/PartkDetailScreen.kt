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
import androidx.compose.ui.draw.scale
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
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    val api = remember { ParkApi() }
    var parkData by remember { mutableStateOf<QueueTimesResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val uriHandler = LocalUriHandler.current

    // --- LÓGICA DE IDIOMA ---
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // Diccionario de traducciones
    val txtSearch = if (isEs) "Buscar atracción..." else "Search attraction..."
    val txtHideClosed = if (isEs) "Ocultar cerradas" else "Hide closed"
    val txtSortWait = if (isEs) "Más espera primero" else "Wait time: High to Low"
    val txtNoData = if (isEs) "No hay datos disponibles." else "No data available."
    val txtWaitMin = if (isEs) "min" else "min"
    val txtClosed = if (isEs) "Cerrado" else "Closed"
    val txtMagicRec = if (isEs) "✨ Recomendación Mágica" else "✨ Magic Recommendation"
    val txtGoNow = if (isEs) "¡Ve ahora!" else "Go now!"

    // --- ESTADOS DE FILTRO Y ORDEN ---
    var searchQuery by remember { mutableStateOf("") }
    var hideClosed by remember { mutableStateOf(false) }
    var sortByWaitTime by remember { mutableStateOf(false) }

    LaunchedEffect(parkId) {
        try {
            isLoading = true
            parkData = if (parkName.contains("Epic", ignoreCase = true)) {
                api.getEpicUniverseData()
            } else {
                api.getParkData(parkId)
            }
        } finally {
            isLoading = false
        }
    }

    MainLayout(
        title = parkName,
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // --- BARRA DE BÚSQUEDA Y FILTROS (TRADUCIDA) ---
            Surface(tonalElevation = 2.dp, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        placeholder = { Text(txtSearch, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, Modifier.size(20.dp)) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hideClosed, onCheckedChange = { hideClosed = it })
                            Text(txtHideClosed, style = MaterialTheme.typography.bodySmall) // TRADUCIDO
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sortByWaitTime, onCheckedChange = { sortByWaitTime = it })
                            Text(txtSortWait, style = MaterialTheme.typography.bodySmall) // TRADUCIDO
                        }
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else if (parkData == null || (parkData?.lands?.isEmpty() == true && parkData?.rides?.isEmpty() == true)) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(txtNoData, textAlign = TextAlign.Center)
                }
            } else {
                val allRidesRaw = (parkData!!.rides + parkData!!.lands.flatMap { it.rides })
                val allFilteredRides = allRidesRaw.filter { ride ->
                    val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true)
                    val matchesOpen = if (hideClosed) ride.is_open else true
                    matchesSearch && matchesOpen
                }.let { if (sortByWaitTime) it.sortedByDescending { r -> r.wait_time } else it }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Recomendación Mágica
                    if (searchQuery.isEmpty() && !sortByWaitTime) {
                        val recommendation = allRidesRaw.filter { it.is_open && it.wait_time > 0 }.minByOrNull { it.wait_time }
                        if (recommendation != null) {
                            item {
                                Card(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(txtMagicRec, fontWeight = FontWeight.Bold, color = Color(0xFFFBC02D))
                                        Text("${recommendation.name} $txtGoNow", style = MaterialTheme.typography.titleMedium)
                                        Text("${recommendation.wait_time} $txtWaitMin", fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }

                    items(allFilteredRides) { attraction ->
                        RideRow(attraction, txtWaitMin, txtClosed)
                        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RideRow(attraction: AttractionAlternative, waitSuffix: String, closedText: String) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(attraction.name, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            text = if (attraction.is_open) "${attraction.wait_time} $waitSuffix" else closedText,
            fontWeight = FontWeight.Bold,
            color = if (attraction.is_open) Color(0xFF4CAF50) else Color.Gray
        )
    }
}