package com.gen.maximizemagic.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.ui.theme.MaximizeMagicTheme
import maximizemagic.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
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
    MaximizeMagicTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. IMAGEN DE FONDO (backgroundMagic.jpg)
            Image(
                painter = painterResource(Res.drawable.backgroundMagic),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.2f // Transparencia para que el contenido sea legible
            )

            Scaffold(
                containerColor = Color.Transparent, // Importante para ver el fondo
                topBar = {
                    Surface(
                        shadowElevation = 4.dp,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .statusBarsPadding()
                                .height(64.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // BOTÓN ATRÁS
                            if (showBackButton) {
                                IconButton(onClick = onBackClick) {
                                    Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                // LOGO DE LA APP (logoApp.png) en lugar de espacio vacío
                                Image(
                                    painter = painterResource(Res.drawable.logoApp),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(40.dp).padding(start = 8.dp)
                                )
                            }

                            // TÍTULO
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                                maxLines = 1
                            )

                            // FOTO DE PERFIL
                            ProfileSection(userPhotoUrl)
                        }
                    }
                }
            ) { paddingValues ->
                content(paddingValues)
            }
        }
    }
}

@Composable
fun ProfileSection(userPhotoUrl: String?) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (!userPhotoUrl.isNullOrEmpty()) {
            KamelImage(
                resource = asyncPainterResource(userPhotoUrl),
                contentDescription = "Perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onLoading = { CircularProgressIndicator(modifier = Modifier.size(16.dp)) },
                onFailure = { Text("👤", fontSize = 16.sp) }
            )
        } else {
            Text("👤", fontSize = 18.sp)
        }
    }
}