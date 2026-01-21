package com.gen.maximizemagic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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

    // Textos base
    val welcomePrefix = if (isEs) "Bienvenido a\n" else "Welcome to\n"
    val appName = "Maximize the Magic"
    val txtGoogle = if (isEs) "Conectarse con Google" else "Connect with Google"
    val txtExit = if (isEs) "Salir" else "Exit"

    // 1. Construcción del texto con dos tamaños diferentes
    val annotatedWelcome = buildAnnotatedString {
        // Parte normal: "Bienvenido a"
        withStyle(style = SpanStyle(fontSize = 32.sp)) {
            append(welcomePrefix)
        }
        // Parte resaltada: "Maximize the Magic" (30% más grande que la anterior aprox)
        withStyle(style = SpanStyle(fontSize = 42.sp, fontWeight = FontWeight.Bold)) {
            append(appName)
        }
    }

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
            // Cambiamos verticalArrangement para controlar la altura manualmente
            verticalArrangement = Arrangement.Top
        ) {
            // 2. ESPACIADOR INICIAL (Ajusta este valor para subir o bajar el bloque)
            // Usamos un Spacer con peso relativo al final para que quede un 20% más arriba del centro.
            Spacer(modifier = Modifier.height(100.dp))

            // 3. CARTEL DE BIENVENIDA
            Text(
                text = annotatedWelcome,
                // Mantenemos headlineLarge para que tome la tipografía Magic del tema
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp),
                lineHeight = 46.sp // Ajuste de interlineado para los distintos tamaños
            )

            // 4. BOTÓN GOOGLE
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

            // 5. BOTÓN SALIR
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

            // 6. ESPACIADOR AL FINAL (Con peso mayor al inicial para empujar el contenido hacia arriba)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}