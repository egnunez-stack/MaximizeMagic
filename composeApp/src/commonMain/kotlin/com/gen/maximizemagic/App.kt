package com.gen.maximizemagic

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gen.maximizemagic.ui.MaximizeMagicScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        // Llamamos a la pantalla principal que diseñamos (la del castillo y botones)
        MaximizeMagicScreen(
            onConnectClick = {
                println("Conectando a Google...")
            },
            onExitClick = {
                println("Saliendo...")
            }
        )
    }
}
