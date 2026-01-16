package com.gen.maximizemagic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.ui.layout.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MainLayout(
    title: String,
    showBackButton: Boolean = false,
    userPhotoUrl: String? = null,
    onBackClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    // LOG DE SEGUIMIENTO
    println("#MaximizeMagic: Renderizando MainLayout. Titulo: $title, FotoURL: ${userPhotoUrl ?: "NULA"}")

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().height(56.dp).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. BOTÓN ATRÁS
                    Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.CenterStart) {
                        if (showBackButton) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                            }
                        }
                    }

                    // 2. TÍTULO
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        maxLines = 1
                    )

                    // 3. FOTO DE PERFIL REAL
                    if (!userPhotoUrl.isNullOrEmpty()) {
                        println("#MaximizeMagic: Intentando cargar KamelImage para $userPhotoUrl")
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            KamelImage(
                                resource = asyncPainterResource(userPhotoUrl),
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                onLoading = {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                },
                                onFailure = {
                                    println("#MaximizeMagic: Error cargando imagen en Kamel")
                                    Text("👤", fontSize = 20.sp)
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
            }
        },
        containerColor = MaximizeMagicCelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}