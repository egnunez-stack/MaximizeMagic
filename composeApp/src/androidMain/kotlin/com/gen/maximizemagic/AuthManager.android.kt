package com.gen.maximizemagic

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

actual class AuthManager actual constructor() {

    actual fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit) {
        val activity = MainActivity.currentActivity
        if (activity == null) {
            onResult(false, null, null)
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken("1064901795856-u2g5m50e1h57unfp1t9kn75n40g4vo9i.apps.googleusercontent.com")
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val account = task.result
                onResult(true, account.displayName, account.photoUrl?.toString())
            } else {
                MainActivity.authCallback = onResult
                val signInIntent = googleSignInClient.signInIntent
                activity.startActivityForResult(signInIntent, 1)
            }
        }
    }

    // --- ADD THIS METHOD TO FIX THE ERROR ---
    actual fun signInWithFacebook(onResult: (Boolean, String?, String?) -> Unit) {
        val activity = MainActivity.currentActivity
        if (activity == null) {
            onResult(false, null, null)
            return
        }

        val callbackManager = CallbackManager.Factory.create()

        // Note: You need to pass the callbackManager to your MainActivity
        // to handle the onActivityResult for Facebook as well.
        MainActivity.facebookCallbackManager = callbackManager

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // Here you would typically fetch user profile data via GraphRequest
                // For now, returning success
                onResult(true, "Facebook User", null)
            }

            override fun onCancel() {
                onResult(false, null, null)
            }

            override fun onError(error: FacebookException) {
                onResult(false, null, null)
            }
        })

        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "email"))
    }
}