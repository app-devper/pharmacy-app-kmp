package app.devper.pharm.presentation.suppliers.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.suppliers.SupplierFormFields
import app.devper.pharm.presentation.suppliers.SupplierFormMode
import app.devper.pharm.presentation.suppliers.SupplierFormUiState
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SupplierFormContent(
    state: SupplierFormUiState,
    callbacks: SupplierFormCallbacks,
) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (state.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = t.colors.accent)
                }
            } else {
                SupplierFormBody(state = state, callbacks = callbacks)
            }
        }
        SupplierFormSaveBar(
            saving = state.saving,
            canSubmit = state.canSubmit,
            onCancel = callbacks.onBack,
            onSubmit = callbacks.onSubmit,
        )
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun SupplierFormBody(
    state: SupplierFormUiState,
    callbacks: SupplierFormCallbacks,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .widthIn(max = 960.dp)
            .padding(PaddingValues(horizontal = 24.dp, vertical = 20.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SupplierFormHeader(state = state, onBack = callbacks.onBack)
        SupplierFormInfoSection(form = state.form, callbacks = callbacks)
    }
}

@Composable
private fun SupplierFormHeader(
    state: SupplierFormUiState,
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
                .padding(horizontal = 4.dp, vertical = 4.dp),
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
