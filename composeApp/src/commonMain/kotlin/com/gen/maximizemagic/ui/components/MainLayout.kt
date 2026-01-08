package com.gen.maximizemagic.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * MainLayout estable que no utiliza APIs experimentales.
 * Reemplaza el TopAppBar experimental con una combinación de Surface y Row.
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
            // Usamos Surface para el fondo y la elevación (sombra)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding() // Evita que la barra quede debajo del notch/reloj
                        .height(64.dp)       // Altura estándar de Material 3
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        // Espaciado lateral para que el texto no pegue al borde si no hay botón
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // El contenido principal de la pantalla se renderiza aquí
        content(paddingValues)
    }
}
