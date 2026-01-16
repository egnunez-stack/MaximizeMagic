package com.gen.maximizemagic.model

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gen.maximizemagic.MainLayout

@Composable
fun AgendaScreen(
    userPhotoUrl: String?,
    onBack: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    MainLayout(
        title = if (isEs) "Agenda Parques" else "Park Schedule",
        showBackButton = true,
        onBackClick = onBack,
        userPhotoUrl = userPhotoUrl
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(if (isEs) "Próximamente: Configura tu calendario de parques aquí."
            else "Coming soon: Configure your park calendar here.")
        }
    }
}