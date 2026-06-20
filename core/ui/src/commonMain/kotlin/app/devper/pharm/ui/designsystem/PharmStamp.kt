package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

enum class PharmStampTone { Neutral, Info, Success, Warning, Danger }

@Immutable
private data class StampColors(val bg: Color, val fg: Color, val border: Color)

@Composable
private fun colorsFor(tone: PharmStampTone): StampColors {
    val t = pharmTokens.colors
    return when (tone) {
        PharmStampTone.Neutral -> StampColors(t.surface, t.fg2, t.border)
        PharmStampTone.Info    -> StampColors(t.accentBgSoft, t.accent, t.accent.copy(alpha = 0.35f))
        PharmStampTone.Success -> StampColors(t.successBg, t.successFg, t.successFg.copy(alpha = 0.35f))
        PharmStampTone.Warning -> StampColors(t.warningBg, t.warningFg, t.warningFg.copy(alpha = 0.40f))
        PharmStampTone.Danger  -> StampColors(t.dangerBg, t.dangerFg, t.dangerFg.copy(alpha = 0.35f))
    }
}

@Composable
fun PharmStamp(
    text: String,
    modifier: Modifier = Modifier,
    tone: PharmStampTone = PharmStampTone.Neutral,
    leadingIcon: ImageVector? = null,
) {
    val c = colorsFor(tone)
    val shape = pharmTokens.shapes.sm
    Row(
        modifier = modifier
            .clip(shape)
            .background(c.bg, shape)
            .border(1.dp, c.border, shape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = c.fg,
                modifier = Modifier.size(12.dp),
            )
        }
        Text(
            text = text,
            style = PharmText.badgeSm.copy(
                color = c.fg,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                fontFeatureSettings = "tnum",
            ),
            softWrap = false,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun PharmStamp_Preview() {
    PharmLightPreview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmStamp("LOT A12345 · EXP 06/2569")
            PharmStamp("LOT B6720 · EXP 12/2568", tone = PharmStampTone.Warning)
            PharmStamp("EXP 01/2571", tone = PharmStampTone.Success)
            PharmStamp("หมดสต็อก", tone = PharmStampTone.Danger)
            PharmStamp("ขย.10", tone = PharmStampTone.Info)
        }
    }
}
