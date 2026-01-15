package com.gen.maximizemagic

expect class AuthManager() {
    fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit)
}