package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmBadgeTone {
    Gray, Green, Amber, Red, Blue, Purple, Emerald, Orange, Indigo, Rose, Teal,
}

enum class PharmBadgeSize { Sm, Md }

@Immutable
private data class BadgeColors(val bg: Color, val fg: Color, val border: Color?)

@Composable
private fun colorsFor(tone: PharmBadgeTone): BadgeColors {
    val t = pharmTokens.colors
    return when (tone) {
        PharmBadgeTone.Gray    -> BadgeColors(t.borderSubtle,    t.fg2,           null)
        PharmBadgeTone.Green   -> BadgeColors(t.successBg,       t.successFg,     null)
        PharmBadgeTone.Amber   -> BadgeColors(t.warningBg,       t.warningFg,     null)
        PharmBadgeTone.Red     -> BadgeColors(t.dangerBg,        t.dangerFg,      null)
        PharmBadgeTone.Blue    -> BadgeColors(t.infoBg,          t.infoFg,        null)
        PharmBadgeTone.Purple  -> BadgeColors(t.typePurpleBg,    t.typePurpleFg,  null)
        PharmBadgeTone.Emerald -> BadgeColors(t.typeEmeraldBg,   t.typeEmeraldFg, null)
        PharmBadgeTone.Orange  -> BadgeColors(t.typeOrangeBg,    t.typeOrangeFg,  null)
        PharmBadgeTone.Indigo  -> BadgeColors(t.indigoBg,        t.indigoFg,      null)
        PharmBadgeTone.Rose    -> BadgeColors(t.ky11Bg.copy(),   t.ky11Fg,        null)
        PharmBadgeTone.Teal    -> BadgeColors(t.ky12Bg,          t.ky12Fg,        t.ky12Border)
    }
}

@Composable
fun PharmBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: PharmBadgeTone = PharmBadgeTone.Gray,
    size: PharmBadgeSize = PharmBadgeSize.Md,
) {
    val c = colorsFor(tone)
    val padding = when (size) {
        PharmBadgeSize.Sm -> PaddingValues(horizontal = 6.dp,  vertical = 2.dp)
        PharmBadgeSize.Md -> PaddingValues(horizontal = 8.dp,  vertical = 2.dp)
    }
    val shape = pharmTokens.shapes.pill
    val style = if (size == PharmBadgeSize.Sm) PharmText.badgeSm else PharmText.badge

    val base = modifier
        .clip(shape)
        .background(c.bg, shape)
    val withBorder = if (c.border != null) base.border(1.dp, c.border, shape) else base

    Text(
        text = text,
        style = style.copy(color = c.fg),
        modifier = withBorder.padding(padding),
    )
}

@Composable
fun KyBadge(
    form: Int,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens.colors
    val (bg, fg, border) = when (form) {
        9  -> Triple(t.ky9Bg,  t.ky9Fg,  t.ky9Border)
        10 -> Triple(t.ky10Bg, t.ky10Fg, t.ky10Border)
        11 -> Triple(t.ky11Bg, t.ky11Fg, t.ky11Border)
        12 -> Triple(t.ky12Bg, t.ky12Fg, t.ky12Border)
        else -> Triple(t.borderSubtle, t.fg2, t.border)
    }
    val shape = pharmTokens.shapes.sm

    Text(
        text = pharmStrings.kyTabLabel(form),
        style = PharmText.badgeSm.copy(color = fg),
        modifier = modifier
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, border, shape)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
