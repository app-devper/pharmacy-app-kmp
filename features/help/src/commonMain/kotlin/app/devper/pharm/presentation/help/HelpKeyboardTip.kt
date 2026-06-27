package app.devper.pharm.presentation.help

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun KeyboardTipBanner() {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.infoBg)
            .border(1.dp, t.colors.infoFg.copy(alpha = 0.25f), t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = pharmStrings.helpTipsLabel,
            style = PharmText.bodySm.copy(
                color = t.colors.infoFg,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        KeyboardKey("F1")
        Text(
            text = pharmStrings.helpTipFocusSearch,
            style = PharmText.bodySm.copy(color = t.colors.infoFg),
        )
        KeyboardKey("F2")
        Text(
            text = pharmStrings.helpTipPaymentField,
            style = PharmText.bodySm.copy(color = t.colors.infoFg),
        )
        KeyboardKey("F4")
        Text(
            text = pharmStrings.helpTipParkBill,
            style = PharmText.bodySm.copy(color = t.colors.infoFg),
        )
    }
}

@Composable
private fun KeyboardKey(label: String) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(t.colors.surface)
            .border(1.dp, t.colors.infoFg.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = label,
            style = PharmText.micro.copy(
                color = t.colors.infoFg,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}
