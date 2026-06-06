package app.devper.pharm.presentation.ky

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmSubPage
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmacyTheme
import androidx.compose.ui.tooling.preview.Preview

data class Ky9AddCallbacks(
    val onBack: () -> Unit = {},
    val onDate: (String) -> Unit = {},
    val onDrugName: (String) -> Unit = {},
    val onRegNo: (String) -> Unit = {},
    val onUnit: (String) -> Unit = {},
    val onQty: (String) -> Unit = {},
    val onPricePerUnit: (String) -> Unit = {},
    val onSeller: (String) -> Unit = {},
    val onInvoiceNo: (String) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

@Composable
fun Ky9AddContent(
    state: Ky9AddUiState,
    callbacks: Ky9AddCallbacks = Ky9AddCallbacks(),
) {
    PharmSubPage(
        title = "เพิ่มรายการ ขย.9",
        onBack = callbacks.onBack,
        scrollable = true,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        actions = {
            PharmSaveAction(
                saving = state.saving,
                canSubmit = state.canSubmitDraft,
                onSubmit = callbacks.onSubmit,
            )
        },
    ) {
        Column(
            modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = "ข้อมูลรายการ ขย.9") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
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

@Preview
@Composable
private fun Ky9AddContent_Empty_Preview() {
    PharmacyTheme {
        Ky9AddContent(state = Ky9AddUiState())
    }
}

@Preview
@Composable
private fun Ky9AddContent_Filled_Preview() {
    PharmacyTheme {
        Ky9AddContent(
            state = Ky9AddUiState(
                draft = Ky9Draft(
                    date = "2026-06-01",
                    drugName = "Tramadol 50mg",
                    regNo = "1A 123/45",
                    unit = "เม็ด",
                    qty = "100",
                    pricePerUnit = "2.50",
                    seller = "บริษัท ยาดี จำกัด",
                    invoiceNo = "INV-2606-001",
                ),
            ),
        )
    }
}
