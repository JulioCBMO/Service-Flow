package com.example.serviceflow.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Azul500 = Color(0xFF185FA5)
val Azul100 = Color(0xFFB5D4F4)
val Azul050 = Color(0xFFE6F1FB)
val Verde600 = Color(0xFF3B6D11)
val Verde050 = Color(0xFFEAF3DE)
val Amber600 = Color(0xFF854F0B)
val Amber050 = Color(0xFFFAEEDA)
val Cinza100 = Color(0xFFF5F5F0)
val Cinza200 = Color(0xFFE8E8E0)

private val LightColorScheme = lightColorScheme(
    primary = Azul500,
    onPrimary = Color.White,
    primaryContainer = Azul050,
    onPrimaryContainer = Azul500,
    secondary = Color(0xFF0C447C),
    background = Cinza100,
    surface = Color.White,
    surfaceVariant = Cinza200,
    outline = Color(0xFFCCCCC4),
    error = Color(0xFFA32D2D)
)

@Composable
fun ServiceFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
