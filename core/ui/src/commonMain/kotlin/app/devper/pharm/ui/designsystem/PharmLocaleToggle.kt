package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

const val LOCALE_WIRE_TH = "th"
const val LOCALE_WIRE_EN = "en"

@Composable
fun PharmLocaleToggle(
    activeWire: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val s = pharmStrings
    Row(
        modifier = modifier
            .clip(t.shapes.pill)
            .background(t.colors.bgPage)
            .border(1.dp, t.colors.border, t.shapes.pill)
            .padding(2.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LocaleSegment(
            label = s.settingsLocaleTh,
            active = activeWire == LOCALE_WIRE_TH,
            onClick = { onSelect(LOCALE_WIRE_TH) },
        )
        LocaleSegment(
            label = s.settingsLocaleEn,
            active = activeWire == LOCALE_WIRE_EN,
            onClick = { onSelect(LOCALE_WIRE_EN) },
        )
    }
}

@Composable
private fun LocaleSegment(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    val bg = if (active) t.colors.accent else Color.Transparent
    val fg = if (active) t.colors.surface else t.colors.fg2
    Box(
        modifier = Modifier
            .sizeIn(minWidth = 44.dp)
            .heightIn(min = 32.dp)
            .clip(t.shapes.pill)
            .background(bg)
            .selectable(selected = active, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = PharmText.badge.copy(
                color = fg,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            ),
        )
    }
}
