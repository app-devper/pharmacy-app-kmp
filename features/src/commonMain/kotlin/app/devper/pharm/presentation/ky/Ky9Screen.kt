package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Ky9Screen(
    onSwitchForm: (KyFormType) -> Unit = {},
    viewModel: Ky9ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val t = pharmTokens
    val rows = state.entries.map { KyRow.Ky9(it) }
    val totalValue = state.entries.sumOf { it.totalValue }

    Column(
        modifier = Modifier.fillMaxSize().background(t.colors.bgPage).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        KyFormHeader(form = KyFormType.Ky9)
        KyToolbar(
            currentForm = KyFormType.Ky9,
            onSwitchForm = onSwitchForm,
            month = state.month,
            onMonthChange = viewModel::onMonthChange,
            onApply = viewModel::applyFilter,
            onExport = viewModel::exportPdf,
            exporting = state.exporting,
            rowCount = state.entries.size,
            totalValue = totalValue,
            onAddEntry = viewModel::toggleAddForm,
        )

        state.message?.let { msg -> KyMessageBanner(msg, viewModel::dismissMessage) }

        if (state.addFormOpen) {
            Ky9AddFormSection(state = state, viewModel = viewModel)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            when {
                state.loading && state.entries.isEmpty() ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = t.colors.accent)
                    }

                else -> KyTable(rows = rows, formType = KyFormType.Ky9)
            }
        }

        Text(
            text = "ส่งออกเป็นไฟล์ Excel/PDF สำหรับยื่น อย. ตามแบบฟอร์ม กระทรวงสาธารณสุข",
            style = PharmText.micro.copy(color = t.colors.fgMuted),
        )
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}

@Composable
private fun Ky9AddFormSection(state: Ky9UiState, viewModel: Ky9ViewModel) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "เพิ่มรายการ ขย.9",
                style = PharmText.h3.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
            )
            PharmButton(
                onClick = viewModel::toggleAddForm,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            ) {
                Icon(PharmIcons.Close, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormFieldLabeled(
                label = "วันที่ * (YYYY-MM-DD)",
                value = state.draft.date,
                onChange = viewModel::onDate,
                modifier = Modifier.weight(1f),
            )
            FormFieldLabeled(
                label = "เลขที่ใบกำกับภาษี",
                value = state.draft.invoiceNo,
                onChange = viewModel::onInvoiceNo,
                modifier = Modifier.weight(1f),
            )
        }
        FormFieldLabeled(
            label = "ชื่อยา *",
            value = state.draft.drugName,
            onChange = viewModel::onDrugName,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormFieldLabeled(
                label = "เลขทะเบียน",
                value = state.draft.regNo,
                onChange = viewModel::onRegNo,
                modifier = Modifier.weight(1f),
            )
            FormFieldLabeled(
                label = "หน่วย *",
                value = state.draft.unit,
                onChange = viewModel::onUnit,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FormFieldLabeled(
                label = "จำนวน *",
                value = state.draft.qty,
                onChange = viewModel::onQty,
                keyboard = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            FormFieldLabeled(
                label = "ราคาต่อหน่วย *",
                value = state.draft.pricePerUnit,
                onChange = viewModel::onPricePerUnit,
                keyboard = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
        }
        FormFieldLabeled(
            label = "ผู้ขาย",
            value = state.draft.seller,
            onChange = viewModel::onSeller,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.weight(1f))
            PharmButton(
                label = "ยกเลิก",
                onClick = viewModel::toggleAddForm,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Md,
            )
            PharmButton(
                onClick = viewModel::submitAdd,
                enabled = state.canSubmitDraft,
                size = PharmButtonSize.Md,
            ) {
                if (state.saving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = t.colors.surface,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Text("บันทึก", style = PharmText.buttonMd)
                }
            }
        }
    }
}

@Composable
private fun FormFieldLabeled(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboard: KeyboardType = KeyboardType.Text,
) {
    val t = pharmTokens
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = label, style = PharmText.micro.copy(color = t.colors.fg3))
        PharmTextField(
            value = value,
            onValueChange = onChange,
            keyboardType = keyboard,
        )
    }
}

