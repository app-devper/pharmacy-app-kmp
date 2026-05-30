package app.devper.pharm.presentation.customers.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.customers.CustomerFormFields
import app.devper.pharm.presentation.customers.CustomerFormMode
import app.devper.pharm.presentation.customers.CustomerFormUiState
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun CustomerFormContent(
    state: CustomerFormUiState,
    callbacks: CustomerFormCallbacks,
) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (state.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                CustomerFormBody(state = state, callbacks = callbacks)
            }
        }
        CustomerFormSaveBar(
            saving = state.saving,
            canSubmit = state.canSubmit,
            onCancel = callbacks.onBack,
            onSubmit = callbacks.onSubmit,
        )
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun CustomerFormBody(
    state: CustomerFormUiState,
    callbacks: CustomerFormCallbacks,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .widthIn(max = 960.dp)
            .padding(PaddingValues(horizontal = 24.dp, vertical = 20.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CustomerFormHeader(state = state, onBack = callbacks.onBack)
        CustomerFormInfoSection(form = state.form, callbacks = callbacks)
    }
}

@Composable
private fun CustomerFormHeader(
    state: CustomerFormUiState,
    onBack: () -> Unit,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .clip(t.shapes.sm)
                .clickable(onClick = onBack)
                .defaultMinSize(minHeight = 44.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = PharmIcons.ReturnArrow,
                contentDescription = "ย้อนกลับ",
                tint = t.colors.fg3,
                modifier = Modifier.size(16.dp),
            )
            Text(text = "กลับ", style = PharmText.body.copy(color = t.colors.fg3))
        }
        Text(text = "/", style = PharmText.body.copy(color = t.colors.fgMuted))
        Text(text = state.titleLabel, style = PharmText.h1)
    }
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
