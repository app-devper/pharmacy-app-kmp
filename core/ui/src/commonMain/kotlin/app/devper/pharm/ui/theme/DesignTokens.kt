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
    bgPage             = Paper,
    surface            = White,
    surfaceRaised      = White,
    sidebarBg          = White,
    sidebarFg          = AzureDeep,
    sidebarFgMuted     = InkNavy2,
    sidebarItemHover   = LineCoolSoft,
    sidebarItemActive  = AzureSoft,

    fg1                = InkNavy,
    fg2                = InkNavy2,
    fg3                = InkNavy3,
    fgMuted            = InkNavy3,

    border             = LineCool,
    borderSubtle       = LineCoolSoft,
    divider            = LineCoolSoft,

    accent             = AzureDeep,
    accentHover        = AzureDeeper,
    accentBgSoft       = AzureSoft,
    focusRing          = Azure,

    successBg          = OkGreenSoft,   successFg = OkGreen,
    warningBg          = OchreSoft,     warningFg = Ochre,
    dangerBg           = DangerRedSoft, dangerFg  = DangerRed,
    infoBg             = AzureSoft,     infoFg    = AzureDeep,

    price              = InkNavy,
    discount           = Ochre,
    scrim              = Color(0x80000000),

    typePurpleBg       = Purple100,   typePurpleFg  = Purple700,
    typeEmeraldBg      = OkGreenSoft, typeEmeraldFg = OkGreen,
    typeOrangeBg       = OchreSoft,   typeOrangeFg  = Ochre,

    ky9Bg              = AzureSoft,  ky9Fg   = AzureDeep,  ky9Border  = AzureLine,
    ky10Bg             = Purple50,  ky10Fg  = Purple700,  ky10Border = Purple200,
    ky11Bg             = OchreSoft, ky11Fg  = Ochre,      ky11Border = OchreLine,
    ky12Bg             = Teal50,    ky12Fg  = Teal600,    ky12Border = Teal200,

    indigoBg           = Indigo100, indigoFg = Indigo700,
)

internal val DarkPharmColors = LightPharmColors.copy(
    bgPage             = PaperDark,
    surface            = SurfaceDark,
    surfaceRaised      = SurfaceDark,
    sidebarBg          = SidebarDark,
    sidebarFg          = InkNavyDarkFg,
    sidebarFgMuted     = InkNavy3Dark,
    sidebarItemHover   = SurfaceDark,
    sidebarItemActive  = Azure,
    fg1                = InkNavyDarkFg,
    fg2                = InkNavy2Dark,
    fg3                = InkNavy3Dark,
    fgMuted            = InkNavy3Dark,
    border             = LineDark,
    borderSubtle       = LineDarkSoft,
    divider            = LineDarkSoft,
    accent             = AzureLight,
    accentHover        = AzureLight2,
    accentBgSoft       = Color(0x331B83D8),
    focusRing          = AzureLight,
    successBg          = Color(0x331E9E6A), successFg = OkGreenLight,
    warningBg          = Color(0x33C77E1F), warningFg = OchreLight,
    dangerBg           = Color(0x33D6453B), dangerFg  = DangerRedLight,
    infoBg             = Color(0x331B83D8), infoFg    = AzureLight,
    price              = InkNavyDarkFg,
    discount           = OchreLight,
    typePurpleBg       = Color(0x409333EA), typePurpleFg = Purple200,
    typeEmeraldBg      = Color(0x331E9E6A), typeEmeraldFg = OkGreenLight,
    typeOrangeBg       = Color(0x33C77E1F), typeOrangeFg = OchreLight,
    ky9Bg              = Color(0x331B83D8), ky9Fg = AzureLight,  ky9Border  = Color(0x661B83D8),
    ky10Bg             = Color(0x409333EA), ky10Fg = Purple200,  ky10Border = Color(0x999333EA),
    ky11Bg             = Color(0x33C77E1F), ky11Fg = OchreLight, ky11Border = Color(0x66C77E1F),
    ky12Bg             = Color(0x400D9488), ky12Fg = Teal200,    ky12Border = Color(0x990D9488),
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
    val sidebarWidth:  Dp = 224.dp,
    val topbarHeight:  Dp = 57.dp,
    val cartWidth:     Dp = 288.dp,
    val modalMax:      Dp = 448.dp,
    val controlHeight: Dp = 40.dp,
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
