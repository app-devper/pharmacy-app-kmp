package app.devper.pharm.presentation.suppliers.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.suppliers.SupplierFormFields
import app.devper.pharm.presentation.suppliers.SupplierFormMode
import app.devper.pharm.presentation.suppliers.SupplierFormUiState
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmSubPage
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SupplierFormContent(
    state: SupplierFormUiState,
    callbacks: SupplierFormCallbacks,
) {
    val t = pharmTokens
    PharmSubPage(
        title = state.titleLabel,
        onBack = callbacks.onBack,
        scrollable = !state.loading,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        bottomBar = {
            SupplierFormSaveBar(
                saving = state.saving,
                canSubmit = state.canSubmit,
                onCancel = callbacks.onBack,
                onSubmit = callbacks.onSubmit,
            )
        },
    ) {
        if (state.loading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                PharmCircularProgress(color = t.colors.accent)
            }
        } else {
            Column(
                modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SupplierFormInfoSection(form = state.form, callbacks = callbacks)
            }
        }
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
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
