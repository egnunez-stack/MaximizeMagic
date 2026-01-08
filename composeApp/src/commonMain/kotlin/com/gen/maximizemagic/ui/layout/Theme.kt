package com.gen.maximizemagic.ui.layout

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CelesteMain,
    secondary = AmarilloSoft,
    tertiary = VerdePastel,
    background = CelesteBg,
    surface = Color.White,
    error = ErrorPastelFuerte,
    onBackground = TextPrimary,
    onError = ErrorTextDark
)

@Composable
fun MaximizeMagicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        // typography = ...,
        content = content
    )
}
