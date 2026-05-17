package app.devper.pharm.presentation.imports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField

@Composable
internal fun ImportFormHeader(
    state: ImportFormUiState,
    callbacks: ImportFormCallbacks,
    onPickSupplier: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImportLabeledField(label = "ผู้จัดจำหน่าย", required = true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PharmTextField(
                        value = state.form.supplier,
                        onValueChange = callbacks.onSupplier,
                        placeholder = "เช่น บริษัท เอ บี ซี ฟาร์มา",
                        enabled = !state.readOnly,
                    )
                }
                if (!state.readOnly && state.suppliers.isNotEmpty()) {
                    PharmButton(
                        label = "เลือก",
                        onClick = onPickSupplier,
                        variant = PharmButtonVariant.Outline,
                        size = PharmButtonSize.Sm,
                        leadingIcon = { Icon(PharmIcons.Suppliers, contentDescription = null) },
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ImportLabeledField(label = "เลขที่ Invoice", modifier = Modifier.weight(1f)) {
                ImportFormField(
                    value = state.form.invoiceNo,
                    onValueChange = callbacks.onInvoiceNo,
                    placeholder = "ออปชัน",
                    enabled = !state.readOnly,
                )
            }
            ImportLabeledField(label = "วันที่รับ", modifier = Modifier.weight(1f)) {
                ImportFormField(
                    value = state.form.receiveDate,
                    onValueChange = callbacks.onReceiveDate,
                    placeholder = "YYYY-MM-DD",
                    enabled = !state.readOnly,
                )
            }
        }
        ImportLabeledField(label = "หมายเหตุ") {
            Column(modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp, max = 100.dp)) {
                PharmTextField(
                    value = state.form.notes,
                    onValueChange = callbacks.onNotes,
                    enabled = !state.readOnly,
                    singleLine = false,
                    keyboardType = KeyboardType.Text,
                )
            }
        }
    }
}
