package app.devper.pharm.presentation.imports

import app.devper.pharm.ui.components.PharmBreakpoint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun ImportFormHeader(
    state: ImportFormUiState,
    callbacks: ImportFormCallbacks,
    onPickSupplier: () -> Unit,
    showValidation: Boolean = false,
    supplierFocusRequester: FocusRequester = FocusRequester.Default,
) {
    val s = pharmStrings
    val supplierError = showValidation && state.form.supplier.isBlank()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ImportLabeledField(
            label = s.importsFormSupplier,
            required = true,
            error = if (supplierError) s.validationRequired(s.importsFormSupplier) else null,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PharmTextField(
                        value = state.form.supplier,
                        onValueChange = callbacks.onSupplier,
                        placeholder = s.importsFormSupplierPlaceholder,
                        enabled = !state.readOnly,
                        isError = supplierError,
                        modifier = Modifier.focusRequester(supplierFocusRequester),
                    )
                }
                if (!state.readOnly && state.suppliers.isNotEmpty()) {
                    PharmButton(
                        label = s.commonPick,
                        onClick = onPickSupplier,
                        variant = PharmButtonVariant.Outline,
                        size = PharmButtonSize.Sm,
                        leadingIcon = { Icon(PharmIcons.Suppliers, contentDescription = null) },
                    )
                }
            }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val twoCol = maxWidth >= PharmBreakpoint.Medium
            if (twoCol) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ImportLabeledField(label = s.importsHeaderInvoiceNo, modifier = Modifier.weight(1f)) {
                        InvoiceNoField(state, callbacks)
                    }
                    ImportLabeledField(label = s.importsFormReceiveDate, modifier = Modifier.weight(1f)) {
                        ReceiveDateField(state, callbacks)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ImportLabeledField(label = s.importsHeaderInvoiceNo) {
                        InvoiceNoField(state, callbacks)
                    }
                    ImportLabeledField(label = s.importsFormReceiveDate) {
                        ReceiveDateField(state, callbacks)
                    }
                }
            }
        }
        ImportLabeledField(label = s.commonNote) {
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

@Composable
private fun InvoiceNoField(state: ImportFormUiState, callbacks: ImportFormCallbacks) {
    ImportFormField(
        value = state.form.invoiceNo,
        onValueChange = callbacks.onInvoiceNo,
        placeholder = pharmStrings.importsFormHeaderOptions,
        enabled = !state.readOnly,
    )
}

@Composable
private fun ReceiveDateField(state: ImportFormUiState, callbacks: ImportFormCallbacks) {
    ImportFormField(
        value = state.form.receiveDate,
        onValueChange = callbacks.onReceiveDate,
        placeholder = "YYYY-MM-DD",
        enabled = !state.readOnly,
    )
}
