package com.gen.maximizemagic.model

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val txtDirections = if (isEs) "Seleccionar punto de inicio" else "Select starting point"
    val txtTickets = if (isEs) "Desde Boletería (si estás lejos)" else "From Ticket Office (if far away)"
    val txtCurrentPos = if (isEs) "Mi posición actual (GPS)" else "My current location (GPS)"

    // --- ESTADOS DE RUTA ---
    var showRouteDialog by remember { mutableStateOf(false) }
    var selectedRideForRoute by remember { mutableStateOf<AttractionAlternative?>(null) }

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

    // --- FUNCIÓN DE NAVEGACIÓN INTELIGENTE ---
    val openGoogleMaps: (Boolean) -> Unit = { useCurrentLocation ->
        val rideName = selectedRideForRoute?.name ?: ""
        val destination = "$rideName $parkName".replace(" ", "+")

        // Si useCurrentLocation es true, origin va vacío (Google Maps usa el GPS actual)
        // Si es false, forzamos la "Boletería" (Tickets) del parque
        val origin = if (useCurrentLocation) "" else "$parkName+Tickets"

        val url = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&travelmode=walking"
        uriHandler.openUri(url)
    }

    // --- DIÁLOGO DE SELECCIÓN DE RUTA ---
    if (showRouteDialog && selectedRideForRoute != null) {
        AlertDialog(
            onDismissRequest = { showRouteDialog = false },
            title = { Text(txtDirections, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Opción 1: GPS Actual (Ideal para cuando estás adentro o en el parking)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            openGoogleMaps(true)
                            showRouteDialog = false
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Azul claro
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(txtCurrentPos, fontWeight = FontWeight.Bold)
                            Text(
                                if(isEs) "Usa tu ubicación en tiempo real" else "Uses your real-time location",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Opción 2: Boletería (Ideal para cuando estás lejos o planificando)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            openGoogleMaps(false)
                            showRouteDialog = false
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Verde claro
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(txtTickets, fontWeight = FontWeight.Bold)
                            Text(
                                if(isEs) "Ruta desde la entrada del parque" else "Route from the park entrance",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRouteDialog = false }) { Text(if (isEs) "Cerrar" else "Close") }
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

            // --- BARRA DE BÚSQUEDA Y FILTROS ---
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
                                    modifier = Modifier.padding(16.dp).fillMaxWidth().clickable {
                                        selectedRideForRoute = recommendation
                                        showRouteDialog = true
                                    },
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
                        RideRow(
                            attraction = attraction,
                            waitSuffix = txtWaitMin,
                            closedText = txtClosed,
                            onClick = {
                                selectedRideForRoute = attraction
                                showRouteDialog = true
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RideRow(
    attraction: AttractionAlternative,
    waitSuffix: String,
    closedText: String,
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
            Text(attraction.name, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            text = if (attraction.is_open) "${attraction.wait_time} $waitSuffix" else closedText,
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