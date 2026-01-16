package com.gen.maximizemagic.model

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gen.maximizemagic.MainLayout

@Composable
fun SettingsScreen(
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    MainLayout(
        title = "Configuración",
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado uniforme entre botones
        ) {
            // 1. Botón Configurar Hogar
            Button(
                onClick = { /* Lógica futura */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("🏠 Configurar Hogar")
            }

            // 2. Botón Configurar Vuelo de Llegada
            Button(
                onClick = { /* Lógica futura */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("🛬 Configurar Vuelo de Llegada")
            }

            // 3. Botón Configurar Vuelo de Partida
            Button(
                onClick = { /* Lógica futura */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("🛫 Configurar Vuelo de Partida")
            }

            // 4. Botón Configurar Idioma
            Button(
                onClick = { /* Lógica futura */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("🌐 Configurar Idioma")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Configura tus datos para optimizar los cálculos de tus traslados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}