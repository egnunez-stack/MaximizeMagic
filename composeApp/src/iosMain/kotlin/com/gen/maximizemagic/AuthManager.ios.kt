package com.gen.maximizemagic

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import cocoapods.GoogleSignIn.GIDSignIn

actual class AuthManager actual constructor() {

    actual fun signInWithGoogle(onResult: (Boolean, String?, String?) -> Unit) {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (rootViewController == null) {
            onResult(false, null, null)
            return
        }

        // En las versiones nuevas, GIDSignIn toma la configuración directamente del GoogleService-Info.plist
        // siempre que el URL Scheme esté configurado en Xcode.
        
        GIDSignIn.sharedInstance.signInWithPresentingViewController(rootViewController) { result, error ->
            if (error != null) {
                println("iOS Google SignIn Error: ${error.localizedDescription}")
                onResult(false, null, null)
            } else {
                val user = result?.user
                val profile = user?.profile
                onResult(true, profile?.name, profile?.imageURLWithDimension(100u)?.absoluteString)
            }
        }
    }

    actual fun signInWithFacebook(onResult: (Boolean, String?, String?) -> Unit) {
        onResult(false, null, null)
    }
}
