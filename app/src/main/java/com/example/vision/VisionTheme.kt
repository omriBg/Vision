package com.example.vision

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * קובץ הגדרות עיצוב מתקדם לאפליקציה
 * גישה מרכזית לצבעים, טיפוגרפיה ועיצוב
 */

// צבעים משופרים
val AfekaGreen = Color(0xFF4CAF50)
val AfekaGreenDark = Color(0xFF087f23)
val AfekaGreenLight = Color(0xFF80e27e)

val AfekaBlue = Color(0xFF1976D2)
val AfekaBlueDark = Color(0xFF004ba0)
val AfekaBlueLight = Color(0xFF63a4ff)

val AfekaAccent = Color(0xFF6A0DAD)
val AfekaAccentDark = Color(0xFF38006b)
val AfekaAccentLight = Color(0xFF9c4dff)

// סכמת צבעים בהירה
private val LightColorScheme = lightColorScheme(
    primary = AfekaBlue,
    onPrimary = Color.White,
    primaryContainer = AfekaBlueLight.copy(alpha = 0.3f),
    onPrimaryContainer = AfekaBlueDark,

    secondary = AfekaGreen,
    onSecondary = Color.White,
    secondaryContainer = AfekaGreenLight.copy(alpha = 0.3f),
    onSecondaryContainer = AfekaGreenDark,

    tertiary = AfekaAccent,
    onTertiary = Color.White,
    tertiaryContainer = AfekaAccentLight.copy(alpha = 0.3f),
    onTertiaryContainer = AfekaAccentDark,

    error = Color(0xFFB00020),

    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
)

// סכמת צבעים כהה
private val DarkColorScheme = darkColorScheme(
    primary = AfekaBlueLight,
    onPrimary = Color.Black,
    primaryContainer = AfekaBlueDark,
    onPrimaryContainer = AfekaBlueLight,

    secondary = AfekaGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = AfekaGreenDark,
    onSecondaryContainer = AfekaGreenLight,

    tertiary = AfekaAccentLight,
    onTertiary = Color.Black,
    tertiaryContainer = AfekaAccentDark,
    onTertiaryContainer = AfekaAccentLight,

    error = Color(0xFFCF6679),

    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
)

// מאפייני נגישות - ניתן לשנות בזמן ריצה
data class AccessibilityPreferences(
    val highContrast: Boolean = false,
    val largeText: Boolean = false,
    val reducedMotion: Boolean = false
)

// יוצר אובייקט סטטי לשימוש בכל האפליקציה
val LocalAccessibilityPreferences = staticCompositionLocalOf {
    AccessibilityPreferences()
}

@Composable
fun VisionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accessibilityPreferences: AccessibilityPreferences = AccessibilityPreferences(),
    content: @Composable () -> Unit
) {
    // קביעת סכמת צבעים בהתאם למצב אפל או בהיר
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // התאמת סכמת הצבעים בהתאם להעדפות נגישות
    val adjustedColorScheme = if (accessibilityPreferences.highContrast) {
        adjustForHighContrast(colorScheme, darkTheme)
    } else {
        colorScheme
    }

    // יצירת סביבת עיצוב
    CompositionLocalProvider(
        LocalAccessibilityPreferences provides accessibilityPreferences
    ) {
        MaterialTheme(
            colorScheme = adjustedColorScheme,
            content = content
        )
    }
}

// התאמת צבעים לניגודיות גבוהה
private fun adjustForHighContrast(colorScheme: ColorScheme, isDarkTheme: Boolean): ColorScheme {
    return if (isDarkTheme) {
        colorScheme.copy(
            primary = Color(0xFFFFC107),          // צהוב בולט
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF2C2C2C), // כהה יותר
            onPrimaryContainer = Color(0xFFFFC107),

            secondary = Color(0xFF64FFDA),        // טורקיז בהיר
            onSecondary = Color.Black,

            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White
        )
    } else {
        colorScheme.copy(
            primary = Color(0xFF0000EE),          // כחול קישורים קלאסי
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE6E6FF), // רקע כחול בהיר מאוד
            onPrimaryContainer = Color(0xFF0000AA),

            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black
        )
    }
}