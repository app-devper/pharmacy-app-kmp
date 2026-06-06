package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

data class Ky11AddCallbacks(
    val onBack: () -> Unit = {},
    val onDate: (String) -> Unit = {},
    val onDrugName: (String) -> Unit = {},
    val onRegNo: (String) -> Unit = {},
    val onUnit: (String) -> Unit = {},
    val onQty: (String) -> Unit = {},
    val onBuyerName: (String) -> Unit = {},
    val onPurpose: (String) -> Unit = {},
    val onPharmacist: (String) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

@Composable
fun Ky11AddContent(
    state: Ky11AddUiState,
    callbacks: Ky11AddCallbacks = Ky11AddCallbacks(),
) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = "เพิ่มรายการ ขย.11",
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmitDraft,
                    onSubmit = callbacks.onSubmit,
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = "ข้อมูลรายการ ขย.11") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KyTwoUp(
                        left = {
                            FormField(label = "วันที่ (YYYY-MM-DD)", required = true) {
                                PharmTextField(value = state.draft.date, onValueChange = callbacks.onDate)
                            }
                        },
                        right = {
                            FormField(label = "ชื่อยา", required = true) {
                                PharmTextField(value = state.draft.drugName, onValueChange = callbacks.onDrugName)
                            }
                        },
                    )
                    KyTwoUp(
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
                    FormField(label = "จำนวน", required = true) {
                        PharmTextField(
                            value = state.draft.qty,
                            onValueChange = callbacks.onQty,
                            keyboardType = KeyboardType.Number,
                        )
                    }
                    FormField(label = "ชื่อผู้ซื้อ") {
                        PharmTextField(value = state.draft.buyerName, onValueChange = callbacks.onBuyerName)
                    }
                    FormField(label = "วัตถุประสงค์การใช้") {
                        PharmTextField(
                            value = state.draft.purpose,
                            onValueChange = callbacks.onPurpose,
                            singleLine = false,
                        )
                    }
                    FormField(label = "ชื่อเภสัชกร") {
                        PharmTextField(value = state.draft.pharmacist, onValueChange = callbacks.onPharmacist)
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Preview
@Composable
private fun Ky11AddContent_Empty_Preview() {
    PharmacyTheme {
        Ky11AddContent(state = Ky11AddUiState())
    }
}

@Preview
@Composable
private fun Ky11AddContent_Filled_Preview() {
    PharmacyTheme {
        Ky11AddContent(
            state = Ky11AddUiState(
                draft = Ky11Draft(
                    date = "2026-06-01",
                    drugName = "Pseudoephedrine 60mg",
                    regNo = "1A 555/40",
                    qty = "20",
                    unit = "เม็ด",
                    buyerName = "นาย ก",
                    purpose = "บรรเทาหวัด",
                    pharmacist = "ภ. ข",
                ),
            ),
        )
    }
}
