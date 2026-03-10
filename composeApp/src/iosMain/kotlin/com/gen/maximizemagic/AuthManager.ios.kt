package com.gen.maximizemagic

actual class AuthManager actual constructor() {
    actual fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit) {
        println("iOS: Google Auth no implementado")
        onResult(false, null, null)
    }

    actual fun signInWithFacebook(onResult: (Boolean, String?, String?) -> Unit) {
        println("iOS: Facebook Auth no implementado")
        onResult(false, null, null)
    }
}
