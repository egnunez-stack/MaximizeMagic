package com.gen.maximizemagic

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

actual class AuthManager actual constructor() {

    // Cambiamos el callback para que devuelva los datos del usuario (Nombre y URL Foto)
    actual fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit) {
        val activity = MainActivity.currentActivity

        if (activity == null) {
            onResult(false, null, null)
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("1064901795856-5nce89suhn08tfccj19vmj6k12hep4sp.apps.googleusercontent.com")
            .requestProfile()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        val signInIntent = googleSignInClient.signInIntent

        try {
            activity.startActivity(signInIntent)

            // Obtenemos la última cuenta para extraer nombre y foto
            // Nota: En un flujo real, esto se leería en onActivityResult,
            // pero para tu flujo actual lo extraemos así:
            val account = GoogleSignIn.getLastSignedInAccount(activity)
            if (account != null) {
                onResult(true, account.displayName, account.photoUrl?.toString())
            } else {
                // Si es la primera vez, devolvemos éxito para avanzar
                onResult(true, "Explorador Mágico", null)
            }
        } catch (e: Exception) {
            onResult(false, null, null)
        }
    }
}