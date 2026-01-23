package com.gen.maximizemagic.ui.components

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

@OptIn(ExperimentalMaterial3Api::class)
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
                containerColor = Color.Transparent, // Para poder ver la imagen de fondo
                topBar = {
                    // CONFIGURACIÓN DE TOP BAR AZUL CON LETRAS BLANCAS
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = title,
                                // Aplicamos tipografía Magic y color blanco
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                            )
                        },
                        navigationIcon = {
                            if (showBackButton) {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            }
                            // SE HA ELIMINADO EL BLOQUE "ELSE" QUE MOSTRABA EL LOGO AQUÍ
                        },
                        actions = {
                            // Foto de perfil a la derecha
                            ProfileSection(userPhotoUrl)
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary, // AZUL
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
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
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        if (!userPhotoUrl.isNullOrEmpty()) {
            KamelImage(
                resource = asyncPainterResource(userPhotoUrl),
                contentDescription = "Perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onLoading = { CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White) },
                onFailure = { Text("👤", fontSize = 16.sp, color = Color.White) }
            )
        } else {
            Text("👤", fontSize = 18.sp, color = Color.White)
        }
    }
}