package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmAvatarSize { Sm, Md, Lg }

@Composable
fun PharmAvatarCircle(
    text: String,
    modifier: Modifier = Modifier,
    size: PharmAvatarSize = PharmAvatarSize.Md,
    tone: PharmBadgeTone = PharmBadgeTone.Blue,
) {
    val t = pharmTokens
    val (boxSize, fontSize) = when (size) {
        PharmAvatarSize.Sm -> 24.dp to 11
        PharmAvatarSize.Md -> 32.dp to 14
        PharmAvatarSize.Lg -> 48.dp to 18
    }
    val (bg, fg) = backgroundFor(tone)
    Box(
        modifier = modifier
            .size(boxSize)
            .clip(t.shapes.pill)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsFrom(text),
            style = PharmText.body.copy(
                color = fg,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize.sp,
            ),
        )
    }
}

@Composable
private fun backgroundFor(tone: PharmBadgeTone): Pair<Color, Color> {
    val c = pharmTokens.colors
    return when (tone) {
        PharmBadgeTone.Blue    -> c.accentBgSoft to c.accent
        PharmBadgeTone.Green   -> c.successBg to c.successFg
        PharmBadgeTone.Amber   -> c.warningBg to c.warningFg
        PharmBadgeTone.Red     -> c.dangerBg to c.dangerFg
        PharmBadgeTone.Purple  -> c.typePurpleBg to c.typePurpleFg
        PharmBadgeTone.Emerald -> c.typeEmeraldBg to c.typeEmeraldFg
        PharmBadgeTone.Orange  -> c.typeOrangeBg to c.typeOrangeFg
        PharmBadgeTone.Indigo  -> c.indigoBg to c.indigoFg
        else                   -> c.borderSubtle to c.fg2
    }
}

internal fun initialsFrom(text: String): String {
    val parts = text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty()     -> "?"
        parts.size == 1     -> parts[0].take(2).uppercase()
        else                -> (parts[0].first().toString() + parts[1].first().toString()).uppercase()
    }
}
