package com.gen.maximizemagic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importamos los colores desde el paquete layout donde está tu Color.kt
import com.gen.maximizemagic.ui.layout.*

/**
 * Pantalla principal de bienvenida con el castillo y botones estilizados.
 */
@Composable
fun MaximizeMagicScreen(
    onConnectClick: () -> Unit,
    onExitClick: () -> Unit
) {
    // Aquí showBackButton es false porque es la pantalla de inicio
    MainLayout(
        title = "Maximize the Magic",
        showBackButton = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Icono representativo (Castillo emoji)
            Text(
                text = "🏰",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 2. Texto de Bienvenida en Dorado
            Text(
                text = "Welcome to\nMaximize the Magic",
                color = GoldMagic,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // 3. Botón Conectarse a Google
            Button(
                onClick = onConnectClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Conectarse a Google", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Botón Exit
            Button(
                onClick = onExitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelesteMain,
                    contentColor = Color.White
                )
            ) {
                Text("Exit", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Componente de diseño base que incluye la barra superior.
 * Se corrigió el uso de iconos y paddings para evitar parpadeos visuales.
 */
@Composable
fun MainLayout(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding() // Evita el notch
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contenedor de ancho fijo para el botón para que el título no salte
                    Box(modifier = Modifier.width(48.dp)) {
                        if (showBackButton) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    // Usamos AutoMirrored para que la flecha apunte bien en todos los idiomas
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary
                                )
                            }
                        }
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        containerColor = MaximizeMagicCelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}