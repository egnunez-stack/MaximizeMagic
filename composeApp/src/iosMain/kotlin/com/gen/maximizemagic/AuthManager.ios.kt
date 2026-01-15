package com.gen.maximizemagic

actual class AuthManager actual constructor() {
    actual fun signInWithGoogle(onResult: (Boolean) -> Unit) {
        println("iOS: Google Auth no implementado")
        onResult(true)
    }
}