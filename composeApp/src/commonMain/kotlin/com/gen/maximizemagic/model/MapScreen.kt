package com.gen.maximizemagic.model

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gen.maximizemagic.ui.components.MainLayout

/**
 * Pantalla que muestra el mapa a pantalla completa.
 * Utiliza el MainLayout para incluir la flecha de navegación hacia atrás.
 */
@Composable
fun MapScreen(url: String, onBack: () -> Unit) {
    MainLayout(
        title = "Ruta Mágica",
        showBackButton = true,
        onBackClick = onBack
    ) { paddingValues ->
        MapView(
            url = url,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

/**
 * Componente de mapa multiplataforma.
 */
@Composable
expect fun MapView(url: String, modifier: Modifier)
