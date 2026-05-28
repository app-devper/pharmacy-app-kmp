package app.devper.pharm.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

private const val WCAG_AA_NORMAL = 4.5
private const val WCAG_AA_LARGE = 3.0

private fun srgbToLinear(c: Float): Double {
    val v = c.toDouble().coerceIn(0.0, 1.0)
    return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
}

private fun relativeLuminance(color: Color): Double =
    0.2126 * srgbToLinear(color.red) +
        0.7152 * srgbToLinear(color.green) +
        0.0722 * srgbToLinear(color.blue)

private fun contrastRatio(fg: Color, bg: Color): Double {
    val a = relativeLuminance(fg)
    val b = relativeLuminance(bg)
    val lighter = maxOf(a, b)
    val darker = minOf(a, b)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun composite(over: Color, under: Color): Color {
    val a = over.alpha.toDouble().coerceIn(0.0, 1.0)
    val r = (a * over.red + (1 - a) * under.red).toFloat()
    val g = (a * over.green + (1 - a) * under.green).toFloat()
    val b = (a * over.blue + (1 - a) * under.blue).toFloat()
    return Color(r, g, b, 1f)
}

private fun assertContrastAtLeast(
    label: String,
    fg: Color,
    bg: Color,
    minRatio: Double = WCAG_AA_NORMAL,
) {
    val ratio = contrastRatio(fg, bg)
    assertTrue(
        ratio >= minRatio,
        "$label: contrast ${ratio.formatRatio()} < ${minRatio.formatRatio()} (required for WCAG AA)",
    )
}

private fun Double.formatRatio(): String {
    val scaled = (this * 100).toLong()
    return "${scaled / 100}.${(scaled % 100).toString().padStart(2, '0')}:1"
}

class ContrastRatioTest {

    @Test
    fun luminance_extremes() {
        val white = relativeLuminance(Color(1f, 1f, 1f, 1f))
        val black = relativeLuminance(Color(0f, 0f, 0f, 1f))
        assertTrue(white > 0.99, "white luminance ~1.0, got $white")
        assertTrue(black < 0.01, "black luminance ~0.0, got $black")
        val cr = contrastRatio(Color(1f, 1f, 1f, 1f), Color(0f, 0f, 0f, 1f))
        assertTrue(cr in 20.0..21.0, "white/black contrast ~21:1, got $cr")
    }

    @Test
    fun composite_opaque_passes_through() {
        val under = Color(0.1f, 0.2f, 0.3f, 1f)
        val over = Color(0.9f, 0.8f, 0.7f, 1f)
        val out = composite(over, under)
        assertTrue(kotlin.math.abs(out.red - 0.9f) < 0.01f)
        assertTrue(kotlin.math.abs(out.green - 0.8f) < 0.01f)
        assertTrue(kotlin.math.abs(out.blue - 0.7f) < 0.01f)
    }

    @Test
    fun composite_half_alpha_midpoint() {
        val under = Color(0f, 0f, 0f, 1f)
        val over = Color(1f, 1f, 1f, 0.5f)
        val out = composite(over, under)
        assertTrue(out.red in 0.49f..0.51f)
    }

    @Test
    fun light_primary_text_passes_aa() {
        val c = LightPharmColors
        assertContrastAtLeast("light fg1/bgPage", c.fg1, c.bgPage)
        assertContrastAtLeast("light fg1/surface", c.fg1, c.surface)
        assertContrastAtLeast("light fg2/bgPage", c.fg2, c.bgPage)
        assertContrastAtLeast("light fg2/surface", c.fg2, c.surface)
    }

    @Test
    fun dark_primary_text_passes_aa() {
        val c = DarkPharmColors
        assertContrastAtLeast("dark fg1/bgPage", c.fg1, c.bgPage)
        assertContrastAtLeast("dark fg1/surface", c.fg1, c.surface)
        assertContrastAtLeast("dark fg2/bgPage", c.fg2, c.bgPage)
        assertContrastAtLeast("dark fg2/surface", c.fg2, c.surface)
        assertContrastAtLeast("dark fg3/bgPage", c.fg3, c.bgPage)
        assertContrastAtLeast("dark fg3/surface", c.fg3, c.surface)
    }

    @Test
    fun light_badge_text_passes_aa_large() {
        val c = LightPharmColors
        assertContrastAtLeast("light success", c.successFg, composite(c.successBg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light warning", c.warningFg, composite(c.warningBg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light danger",  c.dangerFg,  composite(c.dangerBg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light info",    c.infoFg,    composite(c.infoBg,    c.surface), WCAG_AA_LARGE)
    }

    @Test
    fun dark_badge_text_passes_aa_large() {
        val c = DarkPharmColors
        assertContrastAtLeast("dark success", c.successFg, composite(c.successBg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("dark warning", c.warningFg, composite(c.warningBg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("dark danger",  c.dangerFg,  composite(c.dangerBg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("dark info",    c.infoFg,    composite(c.infoBg,    c.surface), WCAG_AA_LARGE)
    }

    @Test
    fun light_type_badge_passes_aa_large() {
        val c = LightPharmColors
        assertContrastAtLeast("light typePurple",  c.typePurpleFg,  composite(c.typePurpleBg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light typeEmerald", c.typeEmeraldFg, composite(c.typeEmeraldBg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light typeOrange",  c.typeOrangeFg,  composite(c.typeOrangeBg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light indigo",      c.indigoFg,      composite(c.indigoBg,      c.surface), WCAG_AA_LARGE)
    }

    @Test
    fun dark_type_badge_passes_aa_large() {
        val c = DarkPharmColors
        assertContrastAtLeast("dark typePurple",  c.typePurpleFg,  composite(c.typePurpleBg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("dark typeEmerald", c.typeEmeraldFg, composite(c.typeEmeraldBg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("dark typeOrange",  c.typeOrangeFg,  composite(c.typeOrangeBg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("dark indigo",      c.indigoFg,      composite(c.indigoBg,      c.surface), WCAG_AA_LARGE)
    }

    @Test
    fun light_ky_badge_passes_aa_large() {
        val c = LightPharmColors
        assertContrastAtLeast("light ky9",  c.ky9Fg,  composite(c.ky9Bg,  c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light ky10", c.ky10Fg, composite(c.ky10Bg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light ky11", c.ky11Fg, composite(c.ky11Bg, c.surface), WCAG_AA_LARGE)
        assertContrastAtLeast("light ky12", c.ky12Fg, composite(c.ky12Bg, c.surface), WCAG_AA_LARGE)
    }

    @Test
    fun dark_ky_badge_passes_aa_large() {
        val c = DarkPharmColors
        assertContrastAtLeast("dark ky9",  c.ky9Fg,  composite(c.ky9Bg,  c.bgPage), WCAG_AA_LARGE)
        assertContrastAtLeast("dark ky10", c.ky10Fg, composite(c.ky10Bg, c.bgPage), WCAG_AA_LARGE)
        assertContrastAtLeast("dark ky11", c.ky11Fg, composite(c.ky11Bg, c.bgPage), WCAG_AA_LARGE)
        assertContrastAtLeast("dark ky12", c.ky12Fg, composite(c.ky12Bg, c.bgPage), WCAG_AA_LARGE)
    }

    @Test
    fun light_accent_passes_aa() {
        val c = LightPharmColors
        assertContrastAtLeast("light accent/bgPage", c.accent, c.bgPage)
        assertContrastAtLeast("light price/bgPage",  c.price,  c.bgPage)
    }

    @Test
    fun dark_accent_passes_aa() {
        val c = DarkPharmColors
        assertContrastAtLeast("dark accent/bgPage", c.accent, c.bgPage)
        assertContrastAtLeast("dark price/bgPage",  c.price,  c.bgPage)
        assertContrastAtLeast("dark discount/bgPage", c.discount, c.bgPage)
    }

    @Test
    fun dark_fg_muted_passes_aa_large() {
        val c = DarkPharmColors
        assertContrastAtLeast("dark fgMuted/bgPage", c.fgMuted, c.bgPage, WCAG_AA_LARGE)
        assertContrastAtLeast("dark fgMuted/surface", c.fgMuted, c.surface, WCAG_AA_LARGE)
    }

    @Test
    fun dark_ky_border_visible() {
        val c = DarkPharmColors
        val minBorder = 1.5
        assertContrastAtLeast("dark ky9Border/bgPage",  composite(c.ky9Border,  c.bgPage), c.bgPage, minBorder)
        assertContrastAtLeast("dark ky10Border/bgPage", composite(c.ky10Border, c.bgPage), c.bgPage, minBorder)
        assertContrastAtLeast("dark ky11Border/bgPage", composite(c.ky11Border, c.bgPage), c.bgPage, minBorder)
        assertContrastAtLeast("dark ky12Border/bgPage", composite(c.ky12Border, c.bgPage), c.bgPage, minBorder)
    }
}
