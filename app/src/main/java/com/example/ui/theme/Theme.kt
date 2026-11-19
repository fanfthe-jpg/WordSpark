package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
  darkColorScheme(
    primary = CoralGlow,
    onPrimary = Color(0xFF2B0A03),
    primaryContainer = CoralGlowContainer,
    onPrimaryContainer = CoralGlowOnContainer,
    secondary = SkyCobalt,
    onSecondary = Color(0xFF0A1D38),
    secondaryContainer = SkyCobaltContainer,
    onSecondaryContainer = SkyCobaltOnContainer,
    tertiary = MintSage,
    onTertiary = Color(0xFF072617),
    tertiaryContainer = MintSageContainer,
    onTertiaryContainer = MintSageOnContainer,
    background = NightPaper,
    onBackground = IvoryInk,
    surface = NightSurface,
    onSurface = IvoryInk,
    surfaceVariant = NightCard,
    onSurfaceVariant = IvoryMuted,
    outline = NightBorder,
    outlineVariant = NightBorder,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VermilionPrimary,
    onPrimary = Color.White,
    primaryContainer = VermilionContainer,
    onPrimaryContainer = VermilionOnContainer,
    secondary = CobaltSecondary,
    onSecondary = Color.White,
    secondaryContainer = CobaltContainer,
    onSecondaryContainer = CobaltOnContainer,
    tertiary = SageTertiary,
    onTertiary = Color.White,
    tertiaryContainer = SageContainer,
    onTertiaryContainer = SageOnContainer,
    background = PaperCream,
    onBackground = InkCharcoal,
    surface = PaperSurface,
    onSurface = InkCharcoal,
    surfaceVariant = PaperCard,
    onSurfaceVariant = InkMuted,
    outline = PaperBorder,
    outlineVariant = PaperBorder,
  )

val WordSparkShapes =
  Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
  )

@Composable
fun WordSparkTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = WordSparkTypography,
    shapes = WordSparkShapes,
    content = content,
  )
}
