package app.devper.pharm.presentation.expiry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
internal fun ExpiryToolbar(
    window: ExpiryWindow,
    selectedCount: Int,
    writingOff: Boolean,
    callbacks: ExpiryCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmListToolbar(
        title = "ยาใกล้หมดอายุ",
        subtitle = "ตรวจล็อตใกล้หมดอายุ และตัดจำหน่าย",
        modifier = modifier,
        filters = {
            PharmSingleSelectChips(
                chips = ExpiryWindow.entries.map { PharmFilterChip(id = it.name, label = it.label) },
                activeId = window.name,
                onSelect = { id -> callbacks.onWindowChange(ExpiryWindow.valueOf(id)) },
                scrollable = false,
            )
        },
        actions = {
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
        },
    )
}

@Composable
internal fun ExpiryRemainingStat(totalRemaining: Int, modifier: Modifier = Modifier) {
    val t = pharmTokens
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "คงเหลือรวม", style = PharmText.micro.copy(color = t.colors.fg3))
        Text(
            text = "$totalRemaining",
            style = PharmText.micro.copy(color = t.colors.fg1, fontWeight = FontWeight.SemiBold).tabular(),
        )
        Text(text = "หน่วย", style = PharmText.micro.copy(color = t.colors.fg3))
    }
}
