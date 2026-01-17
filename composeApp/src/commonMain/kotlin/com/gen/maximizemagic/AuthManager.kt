package com.gen.maximizemagic

expect class AuthManager() {
    fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit)
    fun signInWithFacebook(onResult: (Boolean, String?, String?) -> Unit)
}