package app.devper.pharm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.devper.pharm.ui.resources.Res
import app.devper.pharm.ui.resources.sarabun_bold
import app.devper.pharm.ui.resources.sarabun_light
import app.devper.pharm.ui.resources.sarabun_medium
import app.devper.pharm.ui.resources.sarabun_regular
import app.devper.pharm.ui.resources.sarabun_semi_bold
import org.jetbrains.compose.resources.Font

@Composable
private fun sarabunFontFamily(): FontFamily = FontFamily(
    Font(Res.font.sarabun_light,     FontWeight.Light),
    Font(Res.font.sarabun_regular,   FontWeight.Normal),
    Font(Res.font.sarabun_medium,    FontWeight.Medium),
    Font(Res.font.sarabun_semi_bold, FontWeight.SemiBold),
    Font(Res.font.sarabun_bold,      FontWeight.Bold),
)

const val TabularNumbers: String = "tnum"

@Composable
fun pharmacyTypography(): Typography {
    val family = sarabunFontFamily()
    val base = Typography()
    fun TextStyle.thai(extraLh: Int = 0): TextStyle =
        copy(fontFamily = family, lineHeight = (lineHeight.value + extraLh).sp)
    return Typography(
        displayLarge   = base.displayLarge.thai(8),
        displayMedium  = base.displayMedium.thai(6),
        displaySmall   = base.displaySmall.thai(6),
        headlineLarge  = base.headlineLarge.thai(4),
        headlineMedium = base.headlineMedium.thai(4),
        headlineSmall  = base.headlineSmall.thai(4),
        titleLarge     = base.titleLarge.thai(2),
        titleMedium    = base.titleMedium.thai(2),
        titleSmall     = base.titleSmall.thai(2),
        bodyLarge      = base.bodyLarge.thai(),
        bodyMedium     = base.bodyMedium.thai(),
        bodySmall      = base.bodySmall.thai(),
        labelLarge     = base.labelLarge.thai(),
        labelMedium    = base.labelMedium.thai(),
        labelSmall     = base.labelSmall.thai(),
    )
}

fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = TabularNumbers)

object PharmText {

    @androidx.compose.runtime.Composable
    private fun base(): TextStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium

    @androidx.compose.runtime.Composable
    @androidx.compose.runtime.ReadOnlyComposable
    private fun scaledSp(value: Float): androidx.compose.ui.unit.TextUnit =
        (value * pharmTokens.fontScale).sp

    val h1: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(18f),
            fontWeight = FontWeight.SemiBold,
            lineHeight = scaledSp(24f),
            color = pharmTokens.colors.fg1,
        )

    val h2: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(16f),
            fontWeight = FontWeight.SemiBold,
            lineHeight = scaledSp(22f),
            color = pharmTokens.colors.fg1,
        )

    val h3: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(14f),
            fontWeight = FontWeight.SemiBold,
            color = pharmTokens.colors.fg1,
        )

    val body: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(14f), color = pharmTokens.colors.fg1)

    val bodySm: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(13f), color = pharmTokens.colors.fg1)

    val meta: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(12f), color = pharmTokens.colors.fg3)

    val micro: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(11f), color = pharmTokens.colors.fg3)

    val thead: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(12f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = pharmTokens.colors.fg3,
        )

    val price: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(16f),
            fontWeight = FontWeight.Bold,
            color = pharmTokens.colors.price,
            fontFeatureSettings = TabularNumbers,
        )

    val total: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(18f),
            fontWeight = FontWeight.Bold,
            color = pharmTokens.colors.fg1,
            fontFeatureSettings = TabularNumbers,
        )

    val metric: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(
            fontSize = scaledSp(20f),
            fontWeight = FontWeight.Bold,
            lineHeight = scaledSp(24f),
            fontFeatureSettings = TabularNumbers,
        )

    val buttonMd: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(14f), fontWeight = FontWeight.Medium)

    val buttonSm: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(13f), fontWeight = FontWeight.Medium)

    val badge: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(12f), fontWeight = FontWeight.Medium)

    val badgeSm: TextStyle
        @androidx.compose.runtime.Composable
        get() = base().copy(fontSize = scaledSp(10f), fontWeight = FontWeight.SemiBold)
}

fun fmtBaht(n: Double): String {
    val isInt = n % 1.0 == 0.0
    val sign = if (n < 0) "-" else ""
    val abs = if (n < 0) -n else n
    val whole = abs.toLong()
    val frac = ((abs - whole) * 100.0 + 0.5).toLong().coerceAtLeast(0)
    val wholeStr = buildString {
        val s = whole.toString()
        for (i in s.indices) {
            if (i > 0 && (s.length - i) % 3 == 0) append(',')
            append(s[i])
        }
    }
    return if (isInt) "฿$sign$wholeStr"
    else "฿$sign$wholeStr." + frac.toString().padStart(2, '0')
}

fun fmtBaht(n: Int): String = fmtBaht(n.toDouble())
