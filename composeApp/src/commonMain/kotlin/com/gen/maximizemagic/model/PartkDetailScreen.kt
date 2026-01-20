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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.network.*
import com.gen.maximizemagic.ui.layout.MainLayout
import kotlinx.datetime.*

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

    // Color Dorado institucional
    val magicGold = Color(0xFFD4AF37)

    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // --- DICCIONARIO ---
    val txtSearch = if (isEs) "Buscar atracción..." else "Search attraction..."
    val txtHideClosed = if (isEs) "Ocultar cerradas" else "Hide closed"
    val txtSortWait = if (isEs) "Más espera primero" else "Wait time: High to Low"
    val txtNoData = if (isEs) "No hay datos disponibles." else "No data available."
    val txtWaitMin = if (isEs) "min" else "min"
    val txtClosed = if (isEs) "Cerrado" else "Closed"
    val txtMagicRec = if (isEs) "✨ Recomendación Mágica" else "✨ Magic Recommendation"
    val txtDirections = if (isEs) "Seleccionar punto de inicio" else "Select starting point"
    val txtPoweredBy = "Powered by Queue-Times.com"

    var showRouteDialog by remember { mutableStateOf(false) }
    var selectedRideForRoute by remember { mutableStateOf<AttractionAlternative?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var hideClosed by remember { mutableStateOf(false) }
    var sortByWaitTime by remember { mutableStateOf(false) }

    // --- LÓGICA DE CARGA DE API ---
    LaunchedEffect(parkId) {
        try {
            isLoading = true
            // Si el nombre contiene "Epic", usamos la nueva lógica para la API de Queue-Times (ID 334)
            if (parkName.contains("Epic", ignoreCase = true)) {
                // Suponiendo que tienes esta función en tu ParkApi o la llamas directamente
                parkData = api.getEpicUniverseData()
            } else {
                parkData = api.getParkData(parkId)
            }
        } catch (e: Exception) {
            println("#MaximizeMagic: Error cargando datos: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    val openGoogleMaps: (Boolean) -> Unit = { useCurrentLocation ->
        val rideName = selectedRideForRoute?.name ?: ""
        val destination = "$rideName $parkName".replace(" ", "+")
        val origin = if (useCurrentLocation) "" else "$parkName+Tickets"
        val url = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&travelmode=walking"
        uriHandler.openUri(url)
    }

    if (showRouteDialog && selectedRideForRoute != null) {
        AlertDialog(
            onDismissRequest = { showRouteDialog = false },
            title = { Text(txtDirections, fontWeight = FontWeight.Bold) },
            confirmButton = {
                TextButton(onClick = { showRouteDialog = false }) { Text(if (isEs) "Cerrar" else "Close") }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { openGoogleMaps(true); showRouteDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isEs) "Mi posición GPS" else "My GPS position")
                    }
                    Button(onClick = { openGoogleMaps(false); showRouteDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isEs) "Desde la entrada" else "From Entrance")
                    }
                }
            }
        )
    }

    MainLayout(
        title = parkName,
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // BARRA DE BÚSQUEDA
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
                            Text(txtHideClosed, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sortByWaitTime, onCheckedChange = { sortByWaitTime = it })
                            Text(txtSortWait, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else if (parkData == null) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text(txtNoData) }
            } else {
                // Aplanar la lista de atracciones (Epic Universe viene por lands)
                val allRidesRaw = (parkData!!.rides + parkData!!.lands.flatMap { it.rides })

                val allFilteredRides = allRidesRaw.filter { ride ->
                    val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true)
                    val matchesOpen = if (hideClosed) ride.is_open else true
                    matchesSearch && matchesOpen
                }.let { if (sortByWaitTime) it.sortedByDescending { r -> r.wait_time } else it }

                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                    // 1. RECOMENDACIÓN MÁGICA
                    if (searchQuery.isEmpty() && !sortByWaitTime) {
                        val recommendation = allRidesRaw.filter { it.is_open && it.wait_time > 0 }.minByOrNull { it.wait_time }
                        if (recommendation != null) {
                            item {
                                Card(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth().clickable {
                                        selectedRideForRoute = recommendation
                                        showRouteDialog = true
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(txtMagicRec, fontWeight = FontWeight.Bold, color = Color(0xFFFBC02D))
                                        Text(
                                            text = recommendation.name,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                color = magicGold, // DORADO
                                                fontSize = 22.sp
                                            )
                                        )
                                        Text("${recommendation.wait_time} $txtWaitMin", fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }

                    // 2. LISTADO DE ATRACCIONES
                    items(allFilteredRides) { attraction ->
                        RideRow(
                            attraction = attraction,
                            waitSuffix = txtWaitMin,
                            closedText = txtClosed,
                            magicGold = magicGold,
                            onClick = {
                                selectedRideForRoute = attraction
                                showRouteDialog = true
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }

                // 3. PIE DE PÁGINA (Créditos API - Obligatorio)
                Text(
                    text = txtPoweredBy,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RideRow(
    attraction: AttractionAlternative,
    waitSuffix: String,
    closedText: String,
    magicGold: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = attraction.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = magicGold, // DORADO
                    fontSize = 18.sp
                )
            )
        }
        Text(
            text = if (attraction.is_open) "${attraction.wait_time} $waitSuffix" else closedText,
            fontWeight = FontWeight.Bold,
            color = if (attraction.is_open) Color(0xFF4CAF50) else Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}