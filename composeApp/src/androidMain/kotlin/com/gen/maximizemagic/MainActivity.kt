package com.gen.maximizemagic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    companion object {
        // Referencia al activity actual para que AuthManager pueda lanzar el Intent
        var currentActivity: ComponentActivity? = null

        // Callback que será ejecutado en App.kt tras la respuesta de Google
        var authCallback: ((Boolean, String?, String?) -> Unit)? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        currentActivity = this

        setContent {
            // Llama a la función principal definida en commonMain
            App()
        }
    }

    /**
     * Esta función captura la respuesta del selector de cuentas de Google.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // El código 1 es el que definimos en el AuthManager.android.kt
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Obtenemos la cuenta de forma exitosa
                val account = task.getResult(ApiException::class.java)

                println("#MaximizeMagic: Cuenta elegida correctamente -> ${account?.displayName}")

                // Devolvemos los datos a App.kt: Exito, Nombre, URL de Foto
                authCallback?.invoke(
                    true,
                    account?.displayName,
                    account?.photoUrl?.toString()
                )

            } catch (e: ApiException) {
                // El error 10 (DEVELOPER_ERROR) se captura aquí si el SHA-1 está mal configurado
                println("#MaximizeMagic: Error de Google API (Código ${e.statusCode}): ${e.message}")
                authCallback?.invoke(false, null, null)

            } catch (e: Exception) {
                // Otros errores (ej. el usuario cerró la ventana sin elegir cuenta)
                println("#MaximizeMagic: Inicio de sesión cancelado o fallido: ${e.message}")
                authCallback?.invoke(false, null, null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiamos las referencias para evitar fugas de memoria
        if (currentActivity == this) {
            currentActivity = null
        }
        authCallback = null
    }
}