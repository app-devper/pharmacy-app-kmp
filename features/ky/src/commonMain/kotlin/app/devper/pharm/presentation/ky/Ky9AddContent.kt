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
import app.devper.pharm.ui.i18n.pharmStrings
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
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = pharmStrings.kyAddCtaWithNumber(9),
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = pharmStrings.kyFormInfoSection(9)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyDateYmd, required = true) {
                                PharmTextField(value = state.draft.date, onValueChange = callbacks.onDate)
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.kyTaxInvoiceNo) {
                                PharmTextField(value = state.draft.invoiceNo, onValueChange = callbacks.onInvoiceNo)
                            }
                        },
                    )
                    FormField(label = pharmStrings.kyHeaderItem, required = true) {
                        PharmTextField(value = state.draft.drugName, onValueChange = callbacks.onDrugName)
                    }
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyDrugRegistration) {
                                PharmTextField(value = state.draft.regNo, onValueChange = callbacks.onRegNo)
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.commonUnit, required = true) {
                                PharmTextField(value = state.draft.unit, onValueChange = callbacks.onUnit)
                            }
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.commonQty, required = true) {
                                PharmTextField(
                                    value = state.draft.qty,
                                    onValueChange = callbacks.onQty,
                                    keyboardType = KeyboardType.Number,
                                )
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.kyHeaderPricePerUnit, required = true) {
                                PharmTextField(
                                    value = state.draft.pricePerUnit,
                                    onValueChange = callbacks.onPricePerUnit,
                                    keyboardType = KeyboardType.Decimal,
                                )
                            }
                        },
                    )
                    FormField(label = pharmStrings.kySupplier) {
                        PharmTextField(value = state.draft.seller, onValueChange = callbacks.onSeller)
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
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
