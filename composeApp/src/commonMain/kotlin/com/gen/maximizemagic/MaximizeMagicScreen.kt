package com.gen.maximizemagic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.model.SettingsManager
import com.gen.maximizemagic.ui.layout.MainLayout

@Composable
fun MaximizeMagicScreen(
    onConnectGoogleClick: () -> Unit,
    onConnectFacebookClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    val txtWelcome = if (isEs) "Bienvenido a\nMaximize the Magic" else "Welcome to\nMaximize the Magic"
    val txtGoogle = if (isEs) "Conectarse con Google" else "Connect with Google"
    val txtExit = if (isEs) "Salir" else "Exit"

    MainLayout(
        title = "",
        showBackButton = false
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO ELIMINADO ---

            // 1. CARTEL DE BIENVENIDA
            Text(
                text = txtWelcome,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp),
                lineHeight = 40.sp
            )

            // 2. BOTÓN GOOGLE
            Button(
                onClick = onConnectGoogleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = txtGoogle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. BOTÓN SALIR
            Button(
                onClick = onExitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = txtExit,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}