package app.devper.pharm.presentation.suppliers.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.suppliers.SupplierFormFields
import app.devper.pharm.presentation.suppliers.SupplierFormMode
import app.devper.pharm.presentation.suppliers.SupplierFormUiState
import app.devper.pharm.presentation.suppliers.i18n.localizeSupplierForm
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.pharmFormContentPadding
import app.devper.pharm.ui.designsystem.pharmFormContentWidth
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SupplierFormContent(
    state: SupplierFormUiState,
    callbacks: SupplierFormCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    var validationRequested by remember(state.mode) { mutableStateOf(false) }
    val nameFocusRequester = remember { FocusRequester() }
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = if (state.isEdit) s.suppliersFormEditTitle else s.suppliersFormAddTitle,
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                    onInvalidSubmit = if (state.loading) null else {
                        {
                            validationRequested = true
                            nameFocusRequester.requestFocus()
                        }
                    },
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .then(pharmFormContentWidth())
                .imePadding()
                .verticalScroll(rememberScrollState())
                .pharmFormContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                SupplierFormInfoSection(
                    form = state.form,
                    callbacks = callbacks,
                    showValidation = validationRequested,
                    nameFocusRequester = nameFocusRequester,
                )
            }
        }
    }
    ErrorBottomSheet(message = state.errorState?.localizeSupplierForm(s), onDismiss = callbacks.onDismissError)
}

@Preview
@Composable
private fun SupplierFormContent_Empty_Preview() {
    PharmacyTheme {
        SupplierFormContent(
            state = SupplierFormUiState(),
            callbacks = SupplierFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun SupplierFormContent_Filled_Preview() {
    PharmacyTheme {
        SupplierFormContent(
            state = SupplierFormUiState(
                mode = SupplierFormMode.Edit("s1"),
                form = SupplierFormFields(
                    name = "บริษัท เอ บี ซี ฟาร์มา จำกัด",
                    contactName = "คุณสมชาย",
                    phone = "0812345678",
                    taxId = "0105550000000",
                    address = "999 ถนนพหลโยธิน แขวงจตุจักร เขตจตุจักร กรุงเทพฯ 10900",
                    notes = "ส่งทุกวันอังคารและศุกร์",
                ),
            ),
            callbacks = SupplierFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun SupplierFormContent_Saving_Preview() {
    PharmacyTheme {
        SupplierFormContent(
            state = SupplierFormUiState(
                form = SupplierFormFields(name = "บริษัท ABC"),
                saving = true,
            ),
            callbacks = SupplierFormCallbacks(),
        )
    }
}
