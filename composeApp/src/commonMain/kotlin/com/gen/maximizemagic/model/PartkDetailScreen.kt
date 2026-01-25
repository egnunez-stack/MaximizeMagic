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
import com.gen.maximizemagic.ui.components.AdBanner // Importamos el componente de publicidad
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState

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

    // --- ESTADOS PARA EL MAPA INCRUSTADO ---
    var showEmbeddedMap by remember { mutableStateOf(false) }
    var currentMapUrl by remember { mutableStateOf("") }

    val magicGold = Color(0xFFD4AF37)
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // --- MAPA DE COORDENADAS DE ENTRADAS ACTUALIZADO (Links verificados) ---
    val parkEntrances = remember {
        mapOf(
            "Magic Kingdom" to "28.4161,-81.5812",
            "Disney Hollywood Studios" to "28.3578,-81.5583",
            "Animal Kingdom" to "28.3552,-81.5901",
            "Epcot" to "28.3765,-81.5494",
            "Universal Studios Florida" to "28.4738,-81.4661",
            "Islands of Adventure" to "28.4706,-81.4715",
            "Universal Epic Universe" to "28.4239,-81.4552",
            "Universal Volcano Bay" to "28.4623,-81.4725"
        )
    }

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

    LaunchedEffect(parkId) {
        try {
            isLoading = true
            parkData = if (parkName.contains("Epic", ignoreCase = true)) {
                api.getEpicUniverseData()
            } else {
                api.getParkData(parkId)
            }
        } catch (e: Exception) {
            println("#MaximizeMagic: Error cargando datos: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // --- GENERADOR DE URL MEJORADO CON COORDENADAS FIJAS ---
    fun generateMapUrl(useCurrentLocation: Boolean, rideName: String): String {
        val destination = "$rideName, $parkName".replace(" ", "+")
        val entranceCoords = parkEntrances[parkName] ?: "$parkName, Entrance".replace(" ", "+")
        val origin = if (useCurrentLocation) "My+Location" else entranceCoords

        val url = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&travelmode=walking"
        println("#MaximizeMagic: Intentando cargar URL -> $url")
        return url
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
                    Button(onClick = {
                        currentMapUrl = generateMapUrl(true, selectedRideForRoute!!.name)
                        showEmbeddedMap = true
                        showRouteDialog = false
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isEs) "Mi posición GPS" else "My GPS position")
                    }
                    Button(onClick = {
                        currentMapUrl = generateMapUrl(false, selectedRideForRoute!!.name)
                        showEmbeddedMap = true
                        showRouteDialog = false
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isEs) "Desde la entrada" else "From Entrance")
                    }
                }
            }
        )
    }

    MainLayout(
        title = if (showEmbeddedMap) (selectedRideForRoute?.name ?: parkName) else parkName,
        showBackButton = true,
        onBackClick = {
            if (showEmbeddedMap) showEmbeddedMap = false else onBack()
        },
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        // USAMOS UNA COLUMNA PARA DIVIDIR EL CONTENIDO DE LA PUBLICIDAD
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // 1. EL CONTENIDO PRINCIPAL (LISTA O MAPA) OCUPA TODO EL ESPACIO RESTANTE
            Box(modifier = Modifier.weight(1f)) {

                if (showEmbeddedMap) {
                    // --- VISTA DEL MAPA INCRUSTADO ---
                    val webViewState = rememberWebViewState(currentMapUrl).apply {
                        webSettings.isJavaScriptEnabled = true
                    }
                    val navigator = rememberWebViewNavigator()

                    val lastUrl = webViewState.lastLoadedUrl
                    LaunchedEffect(lastUrl) {
                        if (lastUrl != null && !lastUrl.startsWith("http")) {
                            try {
                                println("#MaximizeMagic: Esquema detectado -> $lastUrl")
                                val cleanUrl = if (lastUrl.startsWith("intent://") && lastUrl.contains("link=")) {
                                    lastUrl.substringAfter("link=").substringBefore("&apn=")
                                        .replace("%3A", ":").replace("%2F", "/").replace("%3F", "?")
                                        .replace("%3D", "=").replace("%26", "&").replace("%2B", "+")
                                } else lastUrl

                                println("#MaximizeMagic: Abriendo URL limpia -> $cleanUrl")
                                uriHandler.openUri(cleanUrl)
                                navigator.navigateBack()
                            } catch (e: Exception) {
                                println("#MaximizeMagic: Error abriendo app externa: ${e.message}")
                            }
                        }
                    }

                    WebView(
                        state = webViewState,
                        navigator = navigator,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // --- VISTA NORMAL DE LA LISTA ---
                    Column(modifier = Modifier.fillMaxSize()) {
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
                            val allRidesRaw = (parkData!!.rides + parkData!!.lands.flatMap { it.rides })
                            val allFilteredRides = allRidesRaw.filter { ride ->
                                val matchesSearch = ride.name.contains(searchQuery, ignoreCase = true)
                                val matchesOpen = if (hideClosed) ride.is_open else true
                                matchesSearch && matchesOpen
                            }.let { if (sortByWaitTime) it.sortedByDescending { r -> r.wait_time } else it }

                            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
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
                                                            color = magicGold,
                                                            fontSize = 22.sp
                                                        )
                                                    )
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
                                        magicGold = magicGold,
                                        onClick = {
                                            selectedRideForRoute = attraction
                                            showRouteDialog = true
                                        }
                                    )
                                    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }

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

            // 2. BANNER DE PUBLICIDAD ADMOB AL PIE
            AdBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black.copy(alpha = 0.05f))
            )
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
                    color = magicGold,
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