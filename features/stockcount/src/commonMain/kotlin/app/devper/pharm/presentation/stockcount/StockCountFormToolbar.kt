package app.devper.pharm.presentation.stockcount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StockCountFormToolbar(
    state: StockCountFormUiState,
    callbacks: StockCountFormCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(t.colors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(modifier = Modifier.widthIn(min = 220.dp, max = 320.dp)) {
                PharmTextField(
                    value = state.query,
                    onValueChange = callbacks.onSearchChange,
                    placeholder = "ค้นหายา / barcode…",
                )
            }
            PharmButton(
                label = "เติมตามระบบ",
                onClick = callbacks.onFillFromSystem,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                enabled = state.drugs.isNotEmpty(),
                leadingIcon = { Icon(PharmIcons.OfflineSync, contentDescription = null) },
            )
            PharmButton(
                label = "ล้าง",
                onClick = callbacks.onClear,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
                enabled = state.counts.isNotEmpty(),
            )
            PharmButton(
                label = "ล้าง draft",
                onClick = callbacks.onClearDraft,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
                enabled = state.counts.isNotEmpty() || state.note.isNotBlank(),
            )
            PharmButton(
                label = "บันทึก ${state.changedCount} รายการ",
                onClick = callbacks.onSave,
                size = PharmButtonSize.Sm,
                enabled = state.canSubmit,
                leadingIcon = { Icon(PharmIcons.Check, contentDescription = null) },
            )
        }
        StockCountFormStatusLine(state = state)
    }
}

@Composable
private fun StockCountFormStatusLine(state: StockCountFormUiState) {
    val t = pharmTokens
    val text = "ทั้งหมด ${state.drugs.size} รายการ · พิมพ์แล้ว ${state.pendingLines.size} · " +
        "แก้ไข ${state.changedCount} · ส่วนต่างรวม ${state.totalAbsDelta}"
    Text(
        text = text,
        style = PharmText.micro.copy(color = t.colors.fg3),
    )
}
