package app.devper.pharm.presentation.labels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.print.PharmLabelCard
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun LabelPreviewPane(size: LabelSize, line: LabelLine?, modifier: Modifier = Modifier) {
    val t = pharmTokens
    val s = pharmStrings
    if (line == null) return
    Column(
        modifier = modifier
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = s.labelsPreviewLabel(size.label),
            style = PharmText.micro.copy(color = t.colors.fg2, fontWeight = FontWeight.SemiBold),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(6) { PharmLabelCard(line = line, size = size) }
        }
    }
}
