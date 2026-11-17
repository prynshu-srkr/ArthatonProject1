package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = Color.White,
    primaryContainer = Teal50,
    onPrimaryContainer = Teal900,
    secondary = Emerald600,
    onSecondary = Color.White,
    secondaryContainer = Emerald50,
    onSecondaryContainer = Emerald800,
    tertiary = Amber500,
    onTertiary = Color.White,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber900,
    error = Rose600,
    onError = Color.White,
    errorContainer = Rose50,
    onErrorContainer = Rose900,
    background = SlateBgLight,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate50,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate300
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal400,
    onPrimary = Zinc950,
    primaryContainer = Teal950,
    onPrimaryContainer = Teal200,
    secondary = Emerald400,
    onSecondary = Zinc950,
    secondaryContainer = Emerald950,
    onSecondaryContainer = Emerald200,
    tertiary = Amber400,
    onTertiary = Zinc950,
    tertiaryContainer = Amber950,
    onTertiaryContainer = Amber200,
    error = Rose400,
    onError = Zinc950,
    errorContainer = Rose950,
    onErrorContainer = Rose300,
    background = DarkBg,
    onBackground = Zinc100,
    surface = Zinc900,
    onSurface = Zinc100,
    surfaceVariant = Zinc950,
    onSurfaceVariant = Zinc400,
    outline = Zinc800,
    outlineVariant = Zinc700
)

@Composable
fun KavachPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
