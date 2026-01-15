package com.gen.maximizemagic

/**
 * Interfaz para manejar la autenticación en ambas plataformas.
 */
expect class AuthManager() { // Constructor vacío para que coincida en ambos lados
    fun signInWithGoogle(onResult: (Boolean) -> Unit)
}