package com.gen.maximizemagic.model

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun MapView(url: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                
                // Configuraciones avanzadas para Google Maps
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true // Crucial para Google Maps
                    databaseEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                
                loadUrl(url)
            }
        },
        update = { webView ->
            // Solo recargar si la URL cambia
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}
