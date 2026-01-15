package com.gen.maximizemagic

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

actual class AuthManager actual constructor() {

    actual fun signInWithGoogle(onResult: (Boolean) -> Unit) {
        val activity = MainActivity.currentActivity

        if (activity == null) {
            println("Error: No se encontró un Activity activo")
            onResult(false)
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("1064901795856-5nce89suhn08tfccj19vmj6k12hep4sp.apps.googleusercontent.com")
            .requestProfile()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // IMPORTANTE: Esto lanza el selector visual de Google
        val signInIntent = googleSignInClient.signInIntent

        try {
            // Lanzamos el selector de cuentas
            activity.startActivity(signInIntent)

            // NOTA: Para una implementación profesional deberías manejar el resultado
            // en onActivityResult, pero por ahora esto hará que aparezca la ventana.
            println("Selector de Google abierto correctamente")
            onResult(true)
        } catch (e: Exception) {
            println("Error al abrir Google Sign In: ${e.message}")
            onResult(false)
        }
    }
}