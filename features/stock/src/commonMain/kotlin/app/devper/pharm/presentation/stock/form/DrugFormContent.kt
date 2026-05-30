package app.devper.pharm.presentation.stock.form

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
import app.devper.pharm.presentation.stock.AltUnitDraft
import app.devper.pharm.presentation.stock.DrugFormFields
import app.devper.pharm.presentation.stock.DrugFormMode
import app.devper.pharm.presentation.stock.DrugFormUiState
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun DrugFormContent(
    state: DrugFormUiState,
    callbacks: DrugFormCallbacks,
) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (state.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                DrugFormBody(state = state, callbacks = callbacks)
            }
        }
        DrugFormSaveBar(
            saving = state.saving,
            canSubmit = state.canSubmit,
            onCancel = callbacks.onBack,
            onSubmit = callbacks.onSubmit,
        )
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun DrugFormBody(
    state: DrugFormUiState,
    callbacks: DrugFormCallbacks,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .widthIn(max = 960.dp)
            .padding(PaddingValues(horizontal = 24.dp, vertical = 20.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DrugFormHeader(state = state, onBack = callbacks.onBack)
        DrugFormDrugInfoSection(form = state.form, callbacks = callbacks)
        when (val mode = state.mode) {
            is DrugFormMode.Add -> {
                DrugFormInitialStockSection(form = state.form, callbacks = callbacks)
            }
            is DrugFormMode.Edit -> {
                DrugFormLotsAndAdjustmentsCard(
                    drugId = mode.drugId,
                    drugName = state.form.name,
                    onOpenLots = callbacks.onOpenLots,
                    onOpenAdjustments = callbacks.onOpenAdjustments,
                )
            }
        }
    }
}

@Composable
private fun DrugFormHeader(
    state: DrugFormUiState,
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

@Composable
private fun DrugFormLotsAndAdjustmentsCard(
    drugId: String,
    drugName: String,
    onOpenLots: (String, String) -> Unit,
    onOpenAdjustments: (String, String) -> Unit,
) {
    FormCard(
        title = "ล็อต & การปรับสต็อก",
        subtitle = "เพิ่ม / ดู / ลบล็อต — รักษาการตรวจสอบ FEFO",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmButton(
                label = "ดูล็อตทั้งหมด",
                onClick = { onOpenLots(drugId, drugName) },
                variant = PharmButtonVariant.Outline,
            )
            PharmButton(
                label = "ปรับปรุงสต็อก",
                onClick = { onOpenAdjustments(drugId, drugName) },
                variant = PharmButtonVariant.Outline,
            )
        }
    }
}

@Preview
@Composable
private fun DrugFormContent_AddEmpty_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(),
            callbacks = DrugFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun DrugFormContent_AddFilled_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(
                form = DrugFormFields(
                    name = "Tylenol 500mg",
                    genericName = "Paracetamol",
                    strength = "500mg",
                    unit = "เม็ด",
                    type = "ยาแผนปัจจุบัน",
                    regNo = "1A 123/45",
                    barcode = "8851234567001",
                    costPrice = "1.2",
                    sellPrice = "2",
                    minStock = "20",
                    reportTypes = setOf("ky10", "ky11"),
                    initialStock = "200",
                    lotNumber = "PCM-260517",
                    lotExpiry = "2026-12-31",
                ),
            ),
            callbacks = DrugFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun DrugFormContent_Edit_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(
                mode = DrugFormMode.Edit("d1"),
                form = DrugFormFields(
                    name = "Paracetamol 500 mg",
                    genericName = "Paracetamol",
                    strength = "500 mg",
                    unit = "เม็ด",
                    type = "ยาแผนปัจจุบัน",
                    sellPrice = "2",
                    costPrice = "1",
                    altUnits = listOf(
                        AltUnitDraft(name = "แผง", factor = "10", sellPrice = "18"),
                    ),
                    reportTypes = setOf("ky9"),
                ),
            ),
            callbacks = DrugFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun DrugFormContent_Saving_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(
                form = DrugFormFields(name = "X", sellPrice = "1"),
                saving = true,
            ),
            callbacks = DrugFormCallbacks(),
        )
    }
}
