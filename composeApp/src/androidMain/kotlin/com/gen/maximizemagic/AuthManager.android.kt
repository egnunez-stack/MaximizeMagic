package com.gen.maximizemagic

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

actual class AuthManager actual constructor() {

    actual fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit) {
        val activity = MainActivity.currentActivity

        if (activity == null) {
            println("#MaximizeMagic: Error - Activity es nulo")
            onResult(false, null, null)
            return
        }

        // 1. Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile() // Necesario para obtener nombre y foto
            // USANDO EL NUEVO ID DE CLIENTE WEB (Obligatorio para que no de Error 10)
            .requestIdToken("1064901795856-u2g5m50e1h57unfp1t9kn75n40g4vo9i.apps.googleusercontent.com")
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // 2. Intentar sesión silenciosa primero (Si el usuario ya se conectó antes)
        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val account = task.result
                println("#MaximizeMagic: Sesión silenciosa exitosa -> ${account.displayName}")
                onResult(true, account.displayName, account.photoUrl?.toString())
            } else {
                // 3. Lanzar selector visual de Google
                println("#MaximizeMagic: Lanzando selector visual de Google...")

                // Guardamos el callback en MainActivity para ejecutarlo cuando el usuario elija su cuenta
                MainActivity.authCallback = onResult

                val signInIntent = googleSignInClient.signInIntent

                // Iniciamos la actividad esperando un resultado (Request Code 1)
                try {
                    activity.startActivityForResult(signInIntent, 1)
                } catch (e: Exception) {
                    println("#MaximizeMagic: Error al iniciar selector visual -> ${e.message}")
                    onResult(false, null, null)
                }
            }
        }
    }
}