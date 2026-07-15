package app.devper.pharm.presentation.suppliers.form

import app.devper.pharm.ui.components.PharmBreakpoint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.suppliers.SupplierFormFields
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun SupplierFormInfoSection(
    form: SupplierFormFields,
    callbacks: SupplierFormCallbacks,
    modifier: Modifier = Modifier,
    showValidation: Boolean = false,
    nameFocusRequester: FocusRequester = FocusRequester.Default,
) {
    val s = pharmStrings
    PharmFormCard(modifier = modifier, title = s.suppliersFormInfoSection) {
        SupplierInfoGrid(
            form = form,
            callbacks = callbacks,
            showValidation = showValidation,
            nameFocusRequester = nameFocusRequester,
        )
        AddressField(value = form.address, onChange = callbacks.onAddress)
        NotesField(value = form.notes, onChange = callbacks.onNotes)
    }
}

@Composable
private fun SupplierInfoGrid(
    form: SupplierFormFields,
    callbacks: SupplierFormCallbacks,
    showValidation: Boolean,
    nameFocusRequester: FocusRequester,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val twoCol = maxWidth >= PharmBreakpoint.FormTwoCol
        if (twoCol) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GridRow(
                    left = { NameField(form, callbacks, showValidation, nameFocusRequester) },
                    right = { ContactNameField(form, callbacks) },
                )
                GridRow(
                    left = { PhoneField(form, callbacks) },
                    right = { TaxIdField(form, callbacks) },
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NameField(form, callbacks, showValidation, nameFocusRequester)
                ContactNameField(form, callbacks)
                PhoneField(form, callbacks)
                TaxIdField(form, callbacks)
            }
        }
    }
}

@Composable
private fun GridRow(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}

@Composable
private fun NameField(
    form: SupplierFormFields,
    callbacks: SupplierFormCallbacks,
    showValidation: Boolean,
    focusRequester: FocusRequester,
) {
    val s = pharmStrings
    val isError = showValidation && form.name.isBlank()
    FormField(
        label = s.suppliersFormCompanyName,
        required = true,
        error = if (isError) s.validationRequired(s.suppliersFormCompanyName) else null,
    ) {
        PharmTextField(
            value = form.name,
            onValueChange = callbacks.onName,
            placeholder = s.suppliersFormCompanyPlaceholder,
            isError = isError,
            modifier = Modifier.focusRequester(focusRequester),
        )
    }
}

@Composable
private fun ContactNameField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
    val s = pharmStrings
    FormField(label = s.suppliersHeaderContact) {
        PharmTextField(
            value = form.contactName,
            onValueChange = callbacks.onContactName,
            placeholder = s.suppliersFormContactName,
        )
    }
}

@Composable
private fun PhoneField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
    val s = pharmStrings
    FormField(label = s.commonPhone) {
        PharmTextField(
            value = form.phone,
            onValueChange = callbacks.onPhone,
            placeholder = "0812345678",
            keyboardType = KeyboardType.Phone,
        )
    }
}

@Composable
private fun TaxIdField(form: SupplierFormFields, callbacks: SupplierFormCallbacks) {
    val s = pharmStrings
    FormField(label = s.suppliersFormTaxId) {
        PharmTextField(
            value = form.taxId,
            onValueChange = callbacks.onTaxId,
            placeholder = pharmStrings.suppliersTaxIdPlaceholder,
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun AddressField(value: String, onChange: (String) -> Unit) {
    val s = pharmStrings
    FormField(label = s.suppliersFormAddress) {
        Box(modifier = Modifier.heightIn(min = 56.dp, max = 120.dp)) {
            PharmTextField(
                value = value,
                onValueChange = onChange,
                placeholder = s.suppliersFormAddressPlaceholder,
                singleLine = false,
            )
        }
    }
}

@Composable
private fun NotesField(value: String, onChange: (String) -> Unit) {
    val s = pharmStrings
    FormField(label = s.commonNote) {
        Box(modifier = Modifier.heightIn(min = 56.dp, max = 120.dp)) {
            PharmTextField(
                value = value,
                onValueChange = onChange,
                placeholder = s.suppliersFormNotesPlaceholder,
                singleLine = false,
            )
        }
    }
}
