package app.devper.pharm.presentation.expiry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ExpiryToolbar(
    window: ExpiryWindow,
    selectedCount: Int,
    writingOff: Boolean,
    callbacks: ExpiryCallbacks,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            PharmSingleSelectChips(
                chips = ExpiryWindow.values().map { PharmFilterChip(id = it.name, label = it.label) },
                activeId = window.name,
                onSelect = { id -> callbacks.onWindowChange(ExpiryWindow.valueOf(id)) },
                scrollable = false,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PharmButton(
                label = "Excel",
                onClick = callbacks.onExportExcel,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Excel, contentDescription = null) },
            )
            if (selectedCount > 0) {
                PharmButton(
                    label = "เขียนทิ้ง $selectedCount รายการ",
                    onClick = callbacks.onAskWriteoff,
                    variant = PharmButtonVariant.Danger,
                    size = PharmButtonSize.Sm,
                    enabled = !writingOff,
                    leadingIcon = { Icon(PharmIcons.Trash, contentDescription = null) },
                )
            }
        }
    }
}

@Composable
internal fun ExpiryResultLine(
    count: Int,
    totalRemaining: Int,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "พบ",
                style = PharmText.micro.copy(color = t.colors.fg3),
            )
            Text(
                text = "$count",
                style = PharmText.micro.copy(
                    color = t.colors.fg1,
                    fontWeight = FontWeight.SemiBold,
                ).tabular(),
            )
            Text(
                text = "ล็อต · คงเหลือรวม",
                style = PharmText.micro.copy(color = t.colors.fg3),
            )
            Text(
                text = "$totalRemaining",
                style = PharmText.micro.copy(
                    color = t.colors.fg1,
                    fontWeight = FontWeight.SemiBold,
                ).tabular(),
            )
            Text(
                text = "หน่วย",
                style = PharmText.micro.copy(color = t.colors.fg3),
            )
        }
    }
}
