package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Ky9Content(
    state: Ky9UiState,
    callbacks: Ky9Callbacks = Ky9Callbacks(),
) {
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
            onSwitchForm = callbacks.onSwitchForm,
            month = state.month,
            onMonthChange = callbacks.onMonthChange,
            onApply = callbacks.onApply,
            onExport = callbacks.onExport,
            exporting = state.exporting,
            rowCount = state.entries.size,
            totalValue = totalValue,
            onAddEntry = callbacks.onToggleAddForm,
        )

        state.message?.let { msg -> KyMessageBanner(msg, callbacks.onDismissMessage) }

        if (state.addFormOpen) {
            Ky9AddFormSection(state = state, callbacks = callbacks)
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
                        PharmCircularProgress(color = t.colors.accent)
                    }

                else -> KyTable(rows = rows, formType = KyFormType.Ky9)
            }
        }

        Text(
            text = "ส่งออกเป็นไฟล์ Excel/PDF สำหรับยื่น อย. ตามแบบฟอร์ม กระทรวงสาธารณสุข",
            style = PharmText.micro.copy(color = t.colors.fgMuted),
        )
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun Ky9AddFormSection(state: Ky9UiState, callbacks: Ky9Callbacks) {
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
                onClick = callbacks.onToggleAddForm,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            ) {
                Icon(PharmIcons.Close, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }
        TwoUpFields(
            left = {
                FormField(label = "วันที่ (YYYY-MM-DD)", required = true) {
                    PharmTextField(value = state.draft.date, onValueChange = callbacks.onDate)
                }
            },
            right = {
                FormField(label = "เลขที่ใบกำกับภาษี") {
                    PharmTextField(value = state.draft.invoiceNo, onValueChange = callbacks.onInvoiceNo)
                }
            },
        )
        FormField(label = "ชื่อยา", required = true) {
            PharmTextField(value = state.draft.drugName, onValueChange = callbacks.onDrugName)
        }
        TwoUpFields(
            left = {
                FormField(label = "เลขทะเบียน") {
                    PharmTextField(value = state.draft.regNo, onValueChange = callbacks.onRegNo)
                }
            },
            right = {
                FormField(label = "หน่วย", required = true) {
                    PharmTextField(value = state.draft.unit, onValueChange = callbacks.onUnit)
                }
            },
        )
        TwoUpFields(
            left = {
                FormField(label = "จำนวน", required = true) {
                    PharmTextField(
                        value = state.draft.qty,
                        onValueChange = callbacks.onQty,
                        keyboardType = KeyboardType.Number,
                    )
                }
            },
            right = {
                FormField(label = "ราคาต่อหน่วย", required = true) {
                    PharmTextField(
                        value = state.draft.pricePerUnit,
                        onValueChange = callbacks.onPricePerUnit,
                        keyboardType = KeyboardType.Decimal,
                    )
                }
            },
        )
        FormField(label = "ผู้ขาย") {
            PharmTextField(value = state.draft.seller, onValueChange = callbacks.onSeller)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.weight(1f))
            PharmButton(
                label = "ยกเลิก",
                onClick = callbacks.onToggleAddForm,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Md,
            )
            PharmButton(
                onClick = callbacks.onSubmitAdd,
                enabled = state.canSubmitDraft,
                size = PharmButtonSize.Md,
            ) {
                if (state.saving) {
                    PharmCircularProgress(
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
private fun TwoUpFields(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth >= 560.dp) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) { left() }
                Box(modifier = Modifier.weight(1f)) { right() }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                left()
                right()
            }
        }
    }
}

private val sampleKy9Draft = Ky9Draft(
    date = "2026-06-01",
    drugName = "Tramadol 50mg",
    regNo = "1A 123/45",
    unit = "เม็ด",
    qty = "100",
    pricePerUnit = "2.50",
    seller = "บริษัท ยาดี จำกัด",
    invoiceNo = "INV-2606-001",
)

private val sampleKy9Entries = listOf(
    Ky9Entry(
        id = "k1",
        saleId = "s1",
        date = "2026-06-01",
        drugName = "Tramadol 50mg",
        regNo = "1A 123/45",
        unit = "เม็ด",
        qty = 100,
        pricePerUnit = 2.5,
        totalValue = 250.0,
        seller = "บริษัท ยาดี จำกัด",
        invoiceNo = "INV-2606-001",
        createdAt = "2026-06-01T09:00:00",
    ),
)

@Preview
@Composable
private fun Ky9Content_ClosedForm_Preview() {
    PharmacyTheme {
        Ky9Content(
            state = Ky9UiState(month = "2026-06", entries = sampleKy9Entries),
        )
    }
}

@Preview
@Composable
private fun Ky9Content_OpenForm_Preview() {
    PharmacyTheme {
        Ky9Content(
            state = Ky9UiState(
                month = "2026-06",
                entries = sampleKy9Entries,
                addFormOpen = true,
                draft = sampleKy9Draft,
            ),
        )
    }
}

@Preview
@Composable
private fun Ky9Content_Saving_Preview() {
    PharmacyTheme {
        Ky9Content(
            state = Ky9UiState(
                month = "2026-06",
                addFormOpen = true,
                draft = sampleKy9Draft,
                saving = true,
            ),
        )
    }
}

@Preview
@Composable
private fun Ky9Content_Loading_Preview() {
    PharmacyTheme {
        Ky9Content(state = Ky9UiState(month = "2026-06", loading = true))
    }
}
