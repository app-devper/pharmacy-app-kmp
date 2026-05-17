package app.devper.pharm.presentation.suppliers.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun SupplierFormSaveBar(
    saving: Boolean,
    canSubmit: Boolean,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(t.colors.surface)
            .border(width = 1.dp, color = t.colors.borderSubtle)
            .padding(PaddingValues(horizontal = 24.dp, vertical = 12.dp)),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmButton(
            label = "ยกเลิก",
            onClick = onCancel,
            variant = PharmButtonVariant.Ghost,
            enabled = !saving,
        )
        PharmButton(
            label = if (saving) "กำลังบันทึก…" else "บันทึก",
            onClick = onSubmit,
            variant = PharmButtonVariant.Primary,
            enabled = canSubmit,
        )
    }
}
