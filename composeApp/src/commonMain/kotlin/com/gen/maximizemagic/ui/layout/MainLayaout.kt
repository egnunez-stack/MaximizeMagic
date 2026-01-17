package com.gen.maximizemagic.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.ui.layout.CelesteBg
import com.gen.maximizemagic.ui.layout.TextPrimary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MainLayout(
    title: String,
    showBackButton: Boolean = false,
    userPhotoUrl: String? = null, // Parámetro para recibir la foto de Google o Facebook
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
                        .statusBarsPadding() // Evita que la barra choque con los iconos del sistema
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. BOTÓN ATRÁS
                    Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) {
                        if (showBackButton) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary
                                )
                            }
                        }
                    }

                    // 2. TÍTULO (Ocupa el espacio central)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    // 3. FOTO DE PERFIL (Lógica para Google y Facebook)
                    if (!userPhotoUrl.isNullOrEmpty()) {
                        // Caso Google: Tiene URL de foto
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            KamelImage(
                                resource = asyncPainterResource(userPhotoUrl),
                                contentDescription = "Perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                onLoading = {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                },
                                onFailure = {
                                    Text("👤", fontSize = 16.sp)
                                }
                            )
                        }
                    } else {
                        // Caso Facebook o Sin Login: Icono por defecto
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 18.sp)
                        }
                    }
                }
            }
        },
        containerColor = CelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}