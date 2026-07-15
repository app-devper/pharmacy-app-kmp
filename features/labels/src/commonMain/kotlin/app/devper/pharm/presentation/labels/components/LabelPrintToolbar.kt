package app.devper.pharm.presentation.labels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.label
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun LabelPrintToolbar(
    size: LabelSize,
    totalCopies: Int,
    canPrint: Boolean,
    canClear: Boolean,
    printing: Boolean,
    onSizeChange: (LabelSize) -> Unit,
    onClearAll: () -> Unit,
    onPrint: () -> Unit,
) {
    val t = pharmTokens
    val s = pharmStrings
    val chips = remember(s) {
        LabelSize.entries.map { PharmFilterChip(id = it.wire, label = it.label(s)) }
    }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = s.labelsSizeLabel,
            style = PharmText.micro.copy(color = t.colors.fg2),
        )
        PharmSingleSelectChips(
            chips = chips,
            activeId = size.wire,
            onSelect = { id -> onSizeChange(LabelSize.fromWire(id)) },
        )
        PharmButton(
            label = s.labelsClear,
            onClick = onClearAll,
            variant = PharmButtonVariant.Ghost,
            size = PharmButtonSize.Sm,
            enabled = canClear,
        )
        PharmButton(
            label = if (printing) s.labelsPrinting else s.labelsPrintCount(totalCopies),
            onClick = onPrint,
            variant = PharmButtonVariant.Primary,
            size = PharmButtonSize.Sm,
            enabled = canPrint || printing,
            loading = printing,
        )
    }
}
