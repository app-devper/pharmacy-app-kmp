package app.devper.pharm.presentation.customers.form

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.customers.CustomerFormFields
import app.devper.pharm.presentation.customers.CustomerFormMode
import app.devper.pharm.presentation.customers.CustomerFormUiState
import app.devper.pharm.presentation.customers.i18n.localizeCustomerForm
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomerFormContent(
    state: CustomerFormUiState,
    callbacks: CustomerFormCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = if (state.isEdit) s.customersFormEditTitle else s.customersAddCta,
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
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
                CustomerFormInfoSection(form = state.form, callbacks = callbacks)
            }
        }
    }
    ErrorBottomSheet(message = state.errorState?.localizeCustomerForm(s), onDismiss = callbacks.onDismissError)
}

@Preview
@Composable
private fun CustomerFormContent_Empty_Preview() {
    PharmacyTheme {
        CustomerFormContent(
            state = CustomerFormUiState(),
            callbacks = CustomerFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun CustomerFormContent_Filled_Preview() {
    PharmacyTheme {
        CustomerFormContent(
            state = CustomerFormUiState(
                mode = CustomerFormMode.Edit("c1"),
                form = CustomerFormFields(
                    name = "สมศรี ใจดี",
                    phone = "0812345678",
                    allergyNote = "แพ้ Penicillin",
                    priceTier = "regular",
                ),
            ),
            callbacks = CustomerFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun CustomerFormContent_Saving_Preview() {
    PharmacyTheme {
        CustomerFormContent(
            state = CustomerFormUiState(
                form = CustomerFormFields(name = "สมชาย"),
                saving = true,
            ),
            callbacks = CustomerFormCallbacks(),
        )
    }
}
