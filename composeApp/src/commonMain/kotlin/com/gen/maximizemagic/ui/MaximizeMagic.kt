package com.gen.maximizemagic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@Composable
fun MaximizeMagicScreen(
    onConnectClick: () -> Unit = {},
    onExitClick: () -> Unit = {}
) {
    MainLayout(title = "Maximize the Magic") { paddingValues ->
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

            // 2. Texto de Bienvenida en Dorado (usando GoldMagic de tu Color.kt)
            Text(
                text = "Welcome to\nMaximize the Magic",
                color = GoldMagic,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // 3. Botón Conectarse a Google (Blanco con sombra)
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

            // 4. Botón Exit (Celeste con letras blancas usando CelesteMain de tu Color.kt)
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
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }
            }
        },
        containerColor = MaximizeMagicCelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}
