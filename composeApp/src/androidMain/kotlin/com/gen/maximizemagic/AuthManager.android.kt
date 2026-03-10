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

        // Web Client ID correcto para el proyecto 953154559379
        val webClientId = "953154559379-kgiv8lt6rtecbgrk4lhgg4fhuvv22vdm.apps.googleusercontent.com" 

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(webClientId)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // Limpiar sesión previa para evitar conflictos y permitir elegir cuenta
        googleSignInClient.signOut().addOnCompleteListener {
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
    }

    actual fun signInWithFacebook(onResult: (Boolean, String?, String?) -> Unit) {
        val activity = MainActivity.currentActivity
        if (activity == null) {
            onResult(false, null, null)
            return
        }

        val callbackManager = CallbackManager.Factory.create()
        MainActivity.facebookCallbackManager = callbackManager

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
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
