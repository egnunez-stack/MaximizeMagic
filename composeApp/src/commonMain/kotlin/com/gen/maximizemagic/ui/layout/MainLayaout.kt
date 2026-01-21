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
                alpha = 0.2f
            )

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    // CONFIGURACIÓN DE TOP BAR AZUL CON LETRAS BLANCAS
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = title,
                                // USAMOS headlineLarge para que tenga la fuente "Magic"
                                // y forzamos el color Blanco.
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 22.sp // Ajustado para que quepa bien en la barra
                                )
                            )
                        },
                        navigationIcon = {
                            if (showBackButton) {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White // Flecha blanca
                                    )
                                }
                            } else {
                                // Logo de la App si no hay botón atrás
                                Image(
                                    painter = painterResource(Res.drawable.logoApp),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(36.dp).padding(start = 8.dp)
                                )
                            }
                        },
                        actions = {
                            // Foto de perfil a la derecha
                            ProfileSection(userPhotoUrl)
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            // FONDO AZUL (Color Primario del Tema)
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
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
            // Fondo traslúcido suave
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