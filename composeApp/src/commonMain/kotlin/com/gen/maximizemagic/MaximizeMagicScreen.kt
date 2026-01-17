package com.gen.maximizemagic.ui

import androidx.compose.foundation.background
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
import com.gen.maximizemagic.ui.layout.*

@Composable
fun MaximizeMagicScreen(
    onConnectGoogleClick: () -> Unit,
    onConnectFacebookClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val settingsManager = remember { SettingsManager() }
    val isEs = settingsManager.language == "es"

    // Textos dinámicos
    val txtWelcome = if (isEs) "Bienvenido a\nMaximize the Magic" else "Welcome to\nMaximize the Magic"
    val txtGoogle = if (isEs) "Conectarse con Google" else "Connect with Google"
    val txtFacebook = if (isEs) "Conectarse con Facebook" else "Connect with Facebook"
    val txtExit = if (isEs) "Salir" else "Exit"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CelesteBg)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Icono / Logo
        Text(
            text = "🏰",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2. Título
        Text(
            text = txtWelcome,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // 3. BOTÓN GOOGLE (Blanco)
        Button(
            onClick = onConnectGoogleClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(txtGoogle, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. BOTÓN FACEBOOK (Comentado)
        /*
        Button(
            onClick = onConnectFacebookClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White
            )
        ) {
            Text(txtFacebook, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        */

        // 5. BOTÓN SALIR (Ahora con el mismo formato que Google pero en color CelesteMain)
        Button(
            onClick = onExitClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CelesteMain,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(txtExit, fontWeight = FontWeight.Bold)
        }
    }
}