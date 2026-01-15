package com.gen.maximizemagic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importamos los colores desde el paquete layout
import com.gen.maximizemagic.ui.layout.*
import maximizemagic.composeapp.generated.resources.Res
import maximizemagic.composeapp.generated.resources.castillo
import org.jetbrains.compose.resources.painterResource

/**
 * Pantalla principal de bienvenida con el castillo y botones estilizados.
 */
@Composable
fun MaximizeMagicScreen(
    onConnectClick: () -> Unit,
    onExitClick: () -> Unit
) {
    // En la pantalla de inicio no mostramos flecha ni foto todavía
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
            // 1. Imagen del Castillo
            Image(
                painter = painterResource(Res.drawable.castillo),
                contentDescription = "Castillo",
                modifier = Modifier.size(250.dp).padding(bottom = 16.dp)
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
 * Se añadió el parámetro userPhotoUrl para mostrar la imagen de Google.
 */
@Composable
fun MainLayout(
    title: String,
    showBackButton: Boolean = false,
    userPhotoUrl: String? = null, // URL de la foto de Google
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
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Atrás
                    Box(modifier = Modifier.width(48.dp)) {
                        if (showBackButton) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary
                                )
                            }
                        }
                    }

                    // Título central/izquierdo
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )

                    // Imagen de Perfil de Google (Derecha)
                    if (userPhotoUrl != null) {
                        // Por ahora mostramos un círculo con un emoji hasta que agregues
                        // una librería de carga de imágenes como Kamel o Coil
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 18.sp)
                        }
                    } else {
                        // Espacio vacío para mantener el equilibrio visual
                        Spacer(modifier = Modifier.width(36.dp))
                    }
                }
            }
        },
        containerColor = MaximizeMagicCelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}