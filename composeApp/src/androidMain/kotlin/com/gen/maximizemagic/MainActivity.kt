package com.gen.maximizemagic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    companion object {
        // Esta referencia estática permite que el AuthManager lance el selector de Google
        var currentActivity: ComponentActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Guardamos la referencia al iniciar
        currentActivity = this

        setContent {
            // Llamamos a la función principal de la app
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiamos la referencia para evitar fugas de memoria
        if (currentActivity == this) {
            currentActivity = null
        }
    }
}