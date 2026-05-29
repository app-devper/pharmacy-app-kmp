package app.devper.pharm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PharmColors(

    val bgPage: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val sidebarBg: Color,
    val sidebarFg: Color,
    val sidebarFgMuted: Color,
    val sidebarItemHover: Color,
    val sidebarItemActive: Color,

    val fg1: Color,
    val fg2: Color,
    val fg3: Color,
    val fgMuted: Color,

    val border: Color,
    val borderSubtle: Color,
    val divider: Color,

    val accent: Color,
    val accentHover: Color,
    val accentBgSoft: Color,
    val focusRing: Color,

    val successBg: Color, val successFg: Color,
    val warningBg: Color, val warningFg: Color,
    val dangerBg: Color, val dangerFg: Color,
    val infoBg: Color, val infoFg: Color,

    val price: Color,
    val discount: Color,
    val scrim: Color,

    val typePurpleBg: Color, val typePurpleFg: Color,
    val typeEmeraldBg: Color, val typeEmeraldFg: Color,
    val typeOrangeBg: Color, val typeOrangeFg: Color,

    val ky9Bg: Color, val ky9Fg: Color, val ky9Border: Color,
    val ky10Bg: Color, val ky10Fg: Color, val ky10Border: Color,
    val ky11Bg: Color, val ky11Fg: Color, val ky11Border: Color,
    val ky12Bg: Color, val ky12Fg: Color, val ky12Border: Color,

    val indigoBg: Color, val indigoFg: Color,
)

internal val LightPharmColors = PharmColors(
    bgPage             = Gray50,
    surface            = White,
    surfaceRaised      = White,
    sidebarBg          = Slate800,
    sidebarFg          = White,
    sidebarFgMuted     = Slate300,
    sidebarItemHover   = Slate700,
    sidebarItemActive  = Blue600,

    fg1                = Gray800,
    fg2                = Gray600,
    fg3                = Gray500,
    fgMuted            = Gray400,

    border             = Gray200,
    borderSubtle       = Gray100,
    divider            = Gray100,

    accent             = Blue600,
    accentHover        = Blue700,
    accentBgSoft       = Blue100,
    focusRing          = Blue400,

    successBg          = Green100, successFg = Green700,
    warningBg          = Amber100, warningFg = Amber700,
    dangerBg           = Red100,   dangerFg  = Red700,
    infoBg             = Blue100,  infoFg    = Blue700,

    price              = Blue600,
    discount           = Rose500,
    scrim              = Color(0x80000000),

    typePurpleBg       = Purple100,  typePurpleFg  = Purple700,
    typeEmeraldBg      = Emerald100, typeEmeraldFg = Emerald700,
    typeOrangeBg       = Orange100,  typeOrangeFg  = Orange700,

    ky9Bg              = Blue50,    ky9Fg   = Blue600,    ky9Border  = Blue200,
    ky10Bg             = Purple50,  ky10Fg  = Purple700,  ky10Border = Purple200,
    ky11Bg             = Red50,     ky11Fg  = Red600,     ky11Border = Red200,
    ky12Bg             = Teal50,    ky12Fg  = Teal600,    ky12Border = Teal200,

    indigoBg           = Indigo100, indigoFg = Indigo700,
)

internal val DarkPharmColors = LightPharmColors.copy(
    bgPage             = Slate900,
    surface            = Slate800,
    surfaceRaised      = Slate800,
    sidebarBg          = Slate950,
    sidebarFg          = White,
    sidebarFgMuted     = Slate400,
    sidebarItemHover   = Slate900,
    sidebarItemActive  = Blue600,
    fg1                = Color(0xFFF1F5F9),
    fg2                = Slate300,
    fg3                = Slate400,
    fgMuted            = Slate400,
    border             = Slate700,
    borderSubtle       = Slate800,
    divider            = Slate800,
    accent             = Blue500,
    accentHover        = Blue400,
    accentBgSoft       = Color(0x403B82F6),
    focusRing          = Blue500,
    successBg          = Color(0x4010B981), successFg = Green400,
    warningBg          = Color(0x40F59E0B), warningFg = Amber400,
    dangerBg           = Color(0x40EF4444),  dangerFg  = Red300,
    infoBg             = Color(0x403B82F6),  infoFg    = Blue400,
    price              = Blue400,
    discount           = Rose400,
    typePurpleBg       = Color(0x409333EA), typePurpleFg = Purple200,
    typeEmeraldBg      = Color(0x4010B981), typeEmeraldFg = Green300,
    typeOrangeBg       = Color(0x40EA580C), typeOrangeFg = Orange300,
    ky9Bg              = Color(0x403B82F6), ky9Fg = Blue400,    ky9Border  = Color(0x993B82F6),
    ky10Bg             = Color(0x409333EA), ky10Fg = Purple200, ky10Border = Color(0x999333EA),
    ky11Bg             = Color(0x40EF4444), ky11Fg = Red300,    ky11Border = Color(0x99EF4444),
    ky12Bg             = Color(0x400D9488), ky12Fg = Teal200,   ky12Border = Color(0x990D9488),
    indigoBg           = Color(0x406366F1), indigoFg = Indigo300,
)

@Immutable
data class PharmSpacing(
    val s0_5: Dp = 2.dp,
    val s1:   Dp = 4.dp,
    val s1_5: Dp = 6.dp,
    val s2:   Dp = 8.dp,
    val s2_5: Dp = 10.dp,
    val s3:   Dp = 12.dp,
    val s4:   Dp = 16.dp,
    val s5:   Dp = 20.dp,
    val s6:   Dp = 24.dp,
    val s8:   Dp = 32.dp,
    val s10:  Dp = 40.dp,
)

@Immutable
data class PharmRadii(
    val sm:   Dp = 4.dp,
    val md:   Dp = 8.dp,
    val lg:   Dp = 12.dp,
    val xl:   Dp = 16.dp,
    val pill: Dp = 9999.dp,
)

@Immutable
data class PharmShapes(
    val sm:   RoundedCornerShape = RoundedCornerShape(4.dp),
    val md:   RoundedCornerShape = RoundedCornerShape(8.dp),
    val lg:   RoundedCornerShape = RoundedCornerShape(12.dp),
    val xl:   RoundedCornerShape = RoundedCornerShape(16.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(9999.dp),
)

@Immutable
data class PharmDimens(
    val sidebarWidth: Dp = 224.dp,
    val topbarHeight: Dp = 57.dp,
    val cartWidth:    Dp = 288.dp,
    val modalMax:     Dp = 448.dp,
)

@Immutable
data class PharmTokens(
    val colors: PharmColors,
    val spacing: PharmSpacing = PharmSpacing(),
    val radii: PharmRadii = PharmRadii(),
    val shapes: PharmShapes = PharmShapes(),
    val dimens: PharmDimens = PharmDimens(),
    val fontScale: Float = 1f,
)

internal val LightPharmTokens = PharmTokens(colors = LightPharmColors)
internal val DarkPharmTokens  = PharmTokens(colors = DarkPharmColors)

val LocalPharmTokens = staticCompositionLocalOf<PharmTokens> { LightPharmTokens }

val pharmTokens: PharmTokens
    @androidx.compose.runtime.Composable
    @androidx.compose.runtime.ReadOnlyComposable
    get() = LocalPharmTokens.current

internal val DefaultPharmTokens = LightPharmTokens
