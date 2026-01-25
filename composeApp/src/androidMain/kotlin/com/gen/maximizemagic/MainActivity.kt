package com.gen.maximizemagic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.facebook.CallbackManager // Importante para Facebook
import com.google.android.gms.ads.MobileAds // Importante para AdMob
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    companion object {
        // Referencia al activity actual para que AuthManager pueda lanzar los Intent
        var currentActivity: ComponentActivity? = null

        // Callback que será ejecutado en App.kt tras la respuesta de Google
        var authCallback: ((Boolean, String?, String?) -> Unit)? = null

        // Gestor de respuestas de Facebook (se usa en AuthManager.android.kt)
        var facebookCallbackManager: CallbackManager? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        currentActivity = this

        // --- INICIALIZACIÓN DE ADMOB ---
        // Se debe inicializar el SDK de anuncios antes de cargar cualquier banner
        MobileAds.initialize(this) { status ->
            println("#MaximizeMagic: AdMob Inicializado correctamente")
        }

        setContent {
            // Llama a la función principal definida en commonMain
            App()
        }
    }

    /**
     * Esta función captura la respuesta tanto de Google como de Facebook.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 1. Manejo de respuesta de Google (Código 1 definido en AuthManager)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                println("#MaximizeMagic: Google OK -> ${account?.displayName}")

                authCallback?.invoke(
                    true,
                    account?.displayName,
                    account?.photoUrl?.toString()
                )
            } catch (e: ApiException) {
                println("#MaximizeMagic: Error Google API (${e.statusCode}): ${e.message}")
                authCallback?.invoke(false, null, null)
            } catch (e: Exception) {
                println("#MaximizeMagic: Google fallido: ${e.message}")
                authCallback?.invoke(false, null, null)
            }
        }

        // 2. Manejo de respuesta de Facebook
        // Esto permite que el CallbackManager procese el resultado y lo mande al AuthManager
        facebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiamos todas las referencias para evitar fugas de memoria
        if (currentActivity == this) {
            currentActivity = null
        }
        authCallback = null
        facebookCallbackManager = null
    }
}