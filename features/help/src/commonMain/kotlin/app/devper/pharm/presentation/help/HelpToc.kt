package app.devper.pharm.presentation.help

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun HelpToc(
    sections: List<HelpSection>,
    activeId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = pharmStrings.helpToc,
            style = PharmText.thead.copy(color = t.colors.fg3),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        sections.forEach { section ->
            val isActive = section.id == activeId
            val fg = if (isActive) t.colors.accent else t.colors.fg2
            val bg = if (isActive) t.colors.accentBgSoft else t.colors.bgPage
            Text(
                text = section.title,
                style = PharmText.bodySm.copy(
                    color = fg,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(t.shapes.md)
                    .background(bg)
                    .clickable(role = Role.Button) { onSelect(section.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}
