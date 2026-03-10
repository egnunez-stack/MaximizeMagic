package com.gen.maximizemagic.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.WebKit.WKWebView
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.CoreGraphics.CGRectZero
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapView(url: String, modifier: Modifier) {
    UIKitView(
        factory = {
            val webView = WKWebView(frame = CGRectZero.readValue())
            val nsUrl = NSURL(string = url)
            if (nsUrl != null) {
                webView.loadRequest(NSURLRequest(uRL = nsUrl))
            }
            webView
        },
        modifier = modifier,
        update = { webView ->
            val nsUrl = NSURL(string = url)
            if (nsUrl != null && webView.URL?.absoluteString != url) {
                webView.loadRequest(NSURLRequest(uRL = nsUrl))
            }
        }
    )
}
