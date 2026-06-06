package app.devper.pharm.presentation.customers.form

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
import app.devper.pharm.presentation.customers.CustomerFormFields
import app.devper.pharm.presentation.customers.CustomerFormMode
import app.devper.pharm.presentation.customers.CustomerFormUiState
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmSubPage
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomerFormContent(
    state: CustomerFormUiState,
    callbacks: CustomerFormCallbacks,
) {
    val t = pharmTokens
    PharmSubPage(
        title = state.titleLabel,
        onBack = callbacks.onBack,
        scrollable = !state.loading,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        bottomBar = {
            CustomerFormSaveBar(
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
                CustomerFormInfoSection(form = state.form, callbacks = callbacks)
            }
        }
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
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
