package com.gen.maximizemagic

actual class AuthManager {
    actual fun signInWithGoogle(onResult: (Boolean) -> Unit) {
        // En iOS se usaría el SDK de Google SignIn para iOS
        println("iOS: Google Auth no implementado aún")
        onResult(true) // Simulación
    }
}