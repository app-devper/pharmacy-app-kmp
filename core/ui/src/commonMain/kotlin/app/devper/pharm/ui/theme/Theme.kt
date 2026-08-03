package app.devper.pharm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary             = Blue600,
    onPrimary           = White,
    primaryContainer    = Blue100,
    onPrimaryContainer  = Blue900,
    inversePrimary      = Blue300,

    secondary           = Indigo600,
    onSecondary         = White,
    secondaryContainer  = Indigo100,
    onSecondaryContainer = Indigo800,

    tertiary            = Amber700,
    onTertiary          = White,
    tertiaryContainer   = Amber100,
    onTertiaryContainer = Amber900,

    background          = Paper,
    onBackground        = InkNavy,

    surface             = White,
    onSurface           = InkNavy,
    surfaceVariant      = Gray100,
    onSurfaceVariant    = InkNavy2,
    surfaceTint         = Blue600,

    surfaceContainerLowest  = White,
    surfaceContainerLow     = Gray50,
    surfaceContainer        = Gray100,
    surfaceContainerHigh    = Gray200,
    surfaceContainerHighest = Slate300,

    inverseSurface      = Slate900,
    inverseOnSurface    = Slate100,

    error               = Red600,
    onError             = White,
    errorContainer      = Red100,
    onErrorContainer    = Red900,

    outline             = Slate300,
    outlineVariant      = Slate200,
    scrim               = Black,
)

private val DarkColors = darkColorScheme(
    primary             = Blue400,
    onPrimary           = Blue900,
    primaryContainer    = Blue800,
    onPrimaryContainer  = Blue100,
    inversePrimary      = Blue600,

    secondary           = Indigo300,
    onSecondary         = Indigo800,
    secondaryContainer  = Indigo700,
    onSecondaryContainer = Indigo100,

    tertiary            = Amber300,
    onTertiary          = Amber900,
    tertiaryContainer   = Amber800,
    onTertiaryContainer = Amber100,

    background          = PaperDark,
    onBackground        = InkNavyDarkFg,

    surface             = SurfaceDark,
    onSurface           = InkNavyDarkFg,
    surfaceVariant      = LineDarkSoft,
    onSurfaceVariant    = InkNavy3Dark,
    surfaceTint         = Blue400,

    surfaceContainerLowest  = PaperDark,
    surfaceContainerLow     = SidebarHoverDark,
    surfaceContainer        = SurfaceDark,
    surfaceContainerHigh    = LineDarkSoft,
    surfaceContainerHighest = LineDark,

    inverseSurface      = Slate100,
    inverseOnSurface    = Slate900,

    error               = Red300,
    onError             = Red900,
    errorContainer      = Red800,
    onErrorContainer    = Red100,

    outline             = LineDark,
    outlineVariant      = LineDarkSoft,
    scrim               = Black,
)

private val PharmacyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun PharmacyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1f,
    touchPrimary: Boolean = false,
    content: @Composable () -> Unit,
) {
    val baseTokens = if (darkTheme) DarkPharmTokens else LightPharmTokens
    val pointerTokens = if (touchPrimary) baseTokens.forTouchInput() else baseTokens
    val tokens = if (fontScale == 1f) pointerTokens else pointerTokens.copy(fontScale = fontScale)
    CompositionLocalProvider(LocalPharmTokens provides tokens) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography  = pharmacyTypography(),
            shapes      = PharmacyShapes,
            content     = content,
        )
    }
}
