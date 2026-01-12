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
fun ParkDetailScreen(parkId: String, parkName: String, onBack: () -> Unit) {
    val api = remember { ParkApi() }
    var parkData by remember { mutableStateOf<QueueTimesResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val uriHandler = LocalUriHandler.current

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
            parkData = api.getParkData(parkId)
        } finally {
            isLoading = false
        }
    }

    val openGoogleMaps: (String, String) -> Unit = { originName, rideName ->
        val destination = "$rideName $parkName".replace(" ", "+")
        val origin = "$parkName $originName".replace(" ", "+")
        val url = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&travelmode=walking"
        uriHandler.openUri(url)
    }

    if (showRouteDialog && selectedRideForRoute != null) {
        AlertDialog(
            onDismissRequest = { showRouteDialog = false },
            title = { Text("Seleccionar punto de inicio", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("¿Desde dónde quieres ver el camino hacia ${selectedRideForRoute!!.name}?")
                    
                    // Opción: Estacionamiento (Color Azul Maps)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            openGoogleMaps("Parking", selectedRideForRoute!!.name)
                            showRouteDialog = false
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = Color(0xFF1976D2) // Azul de ruta
                            ) {
                                Text(
                                    "Desde Estacionamiento",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("📍 Ver ruta azul", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Opción: Boletería (Otro Color - Verde)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            openGoogleMaps("Tickets", selectedRideForRoute!!.name)
                            showRouteDialog = false
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = Color(0xFF388E3C) // Verde
                            ) {
                                Text(
                                    "Desde Boletería",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("📍 Ver ruta verde", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRouteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    MainLayout(title = parkName, showBackButton = true, onBackClick = onBack) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        placeholder = { Text("Buscar...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hideClosed, onCheckedChange = { hideClosed = it }, modifier = Modifier.scale(0.8f))
                            Text("Ocultar cerradas", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sortByWaitTime, onCheckedChange = { sortByWaitTime = it }, modifier = Modifier.scale(0.8f))
                            Text("Más espera primero", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (parkData == null || (parkData?.lands?.isEmpty() == true && parkData?.rides?.isEmpty() == true)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏜️", fontSize = 50.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Datos no disponibles.", textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
                    }
                }
            } else {
                val allRidesRaw = (parkData!!.rides + parkData!!.lands.flatMap { it.rides })
                val allFilteredRides = allRidesRaw.filter { ride ->
                    val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true)
                    val matchesOpen = if (hideClosed) ride.is_open else true
                    matchesSearch && matchesOpen
                }.let { if (sortByWaitTime) it.sortedByDescending { r -> r.wait_time } else it }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (searchQuery.isEmpty() && !sortByWaitTime) {
                        val recommendation = allRidesRaw.filter { it.is_open && it.wait_time > 0 }.minByOrNull { it.wait_time }
                        if (recommendation != null) {
                            item {
                                Card(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth().clickable { 
                                        selectedRideForRoute = recommendation
                                        showRouteDialog = true 
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("✨ Recomendación Mágica", fontWeight = FontWeight.Bold, color = Color(0xFFFBC02D))
                                        Text("¡Ve a ${recommendation.name} ahora!", style = MaterialTheme.typography.titleMedium)
                                        Text("¡Solo ${recommendation.wait_time} min de espera!", fontWeight = FontWeight.ExtraBold)
                                        Text("Haz clic para elegir punto de partida 📍", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    if (sortByWaitTime) {
                        items(allFilteredRides) { attraction ->
                            RideRow(attraction, onClick = { 
                                selectedRideForRoute = attraction
                                showRouteDialog = true 
                            })
                            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    } else {
                        parkData!!.lands.forEach { land ->
                            val ridesInLand = land.rides.filter { ride ->
                                (ride.name.contains(searchQuery, ignoreCase = true) || land.name.contains(searchQuery, ignoreCase = true)) &&
                                (if (hideClosed) ride.is_open else true)
                            }
                            if (ridesInLand.isNotEmpty()) {
                                stickyHeader {
                                    Text(land.name, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                items(ridesInLand) { attraction ->
                                    RideRow(attraction, onClick = { 
                                        selectedRideForRoute = attraction
                                        showRouteDialog = true 
                                    })
                                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                        val standalones = parkData!!.rides.filter { ride ->
                            ride.name.contains(searchQuery, ignoreCase = true) && (if (hideClosed) ride.is_open else true)
                        }
                        if (standalones.isNotEmpty()) {
                            item { Text("Otras Atracciones", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) }
                            items(standalones) { RideRow(it, onClick = { 
                                selectedRideForRoute = it
                                showRouteDialog = true 
                            }) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(Modifier.layout { m, c ->
    val p = m.measure(c)
    layout(p.width, p.height) { p.placeWithLayer(0, 0) { scaleX = scale; scaleY = scale } }
})

@Composable
fun RideRow(attraction: AttractionAlternative, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = attraction.name, style = MaterialTheme.typography.bodyLarge)
            if (attraction.last_updated.isNotEmpty()) {
                Text(text = "Actualizado: ${attraction.last_updated.take(16).replace("T", " ")}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Text(
            text = if (attraction.is_open) "${attraction.wait_time} min" else "Cerrado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                !attraction.is_open -> Color.Gray
                attraction.wait_time > 45 -> Color.Red
                attraction.wait_time < 15 -> Color(0xFF4CAF50)
                else -> Color.Unspecified
            }
        )
    }
}
