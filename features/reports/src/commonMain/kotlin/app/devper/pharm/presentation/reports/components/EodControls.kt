package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.reports.EodCallbacks
import app.devper.pharm.presentation.reports.EodCloseButton
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun EodControls(
    date: String,
    loading: Boolean,
    closing: Boolean,
    closed: Boolean,
    hasReport: Boolean,
    callbacks: EodCallbacks,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormField(
            label = "วันที่",
            hint = "รูปแบบ YYYY-MM-DD (ว่าง = วันนี้)",
            modifier = Modifier.weight(1f),
        ) {
            PharmTextField(
                value = date,
                onValueChange = callbacks.onDateChange,
                placeholder = "YYYY-MM-DD",
                keyboardType = KeyboardType.Number,
            )
        }
        PharmButton(
            label = "ค้นหา",
            onClick = callbacks.onApplyDate,
            variant = PharmButtonVariant.Secondary,
            size = PharmButtonSize.Md,
            enabled = !loading,
        )
        EodCloseButton(
            closed = closed,
            enabled = !loading && !closing && hasReport,
            onClick = callbacks.onRequestClose,
        )
    }
}
