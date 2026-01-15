package com.gen.maximizemagic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gen.maximizemagic.ui.layout.*
import maximizemagic.composeapp.generated.resources.Res
import maximizemagic.composeapp.generated.resources.castillo
import org.jetbrains.compose.resources.painterResource

@Composable
fun MaximizeMagicScreen(
    onConnectClick: () -> Unit,
    onExitClick: () -> Unit
) {
    MainLayout(title = "Maximize the Magic", showBackButton = false) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.castillo),
                contentDescription = "Castillo",
                modifier = Modifier.size(250.dp).padding(bottom = 16.dp)
            )

            Text(
                text = "Welcome to\nMaximize the Magic",
                color = GoldMagic,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Button(
                onClick = onConnectClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Conectarse a Google", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onExitClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CelesteMain, contentColor = Color.White)
            ) {
                Text("Exit", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MainLayout(
    title: String,
    showBackButton: Boolean = false,
    userPhotoUrl: String? = null,
    onBackClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().height(56.dp).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BOTÓN ATRÁS (Izquierda)
                    Box(modifier = Modifier.width(48.dp)) {
                        if (showBackButton) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                            }
                        }
                    }

                    // TÍTULO (Ocupa el resto del espacio empujando lo demás a la derecha)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )

                    // FOTO DE PERFIL (Derecha extrema)
                    if (userPhotoUrl != null) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 18.sp)
                        }
                    }
                }
            }
        },
        containerColor = MaximizeMagicCelesteBg
    ) { paddingValues ->
        content(paddingValues)
    }
}