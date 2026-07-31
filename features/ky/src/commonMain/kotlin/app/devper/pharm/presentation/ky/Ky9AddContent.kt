package app.devper.pharm.presentation.ky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.localizeCommon
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
    val s = pharmStrings
    var validationRequested by remember { mutableStateOf(false) }
    val commonFocus = rememberKyCommonFocusRequesters()
    val priceFocus = remember { FocusRequester() }
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = pharmStrings.kyAddCtaWithNumber(9),
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                    onInvalidSubmit = {
                        validationRequested = true
                        if (!commonFocus.requestFirstInvalid(
                                date = state.draft.date,
                                drugName = state.draft.drugName,
                                unit = state.draft.unit,
                                qty = state.draft.qty,
                            )
                        ) {
                            priceFocus.requestFocus()
                        }
                    },
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = pharmStrings.kyFormInfoSection(9)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KyTwoUp(
                        left = {
                            KyValidatedField(
                                label = s.kyDateYmd,
                                value = state.draft.date,
                                onValueChange = callbacks.onDate,
                                showValidation = validationRequested,
                                focusRequester = commonFocus.date,
                                rule = KyValidationRule.Date,
                            )
                        },
                        right = {
                            FormField(label = pharmStrings.kyTaxInvoiceNo) {
                                PharmTextField(value = state.draft.invoiceNo, onValueChange = callbacks.onInvoiceNo)
                            }
                        },
                    )
                    KyValidatedField(
                        label = s.kyHeaderItem,
                        value = state.draft.drugName,
                        onValueChange = callbacks.onDrugName,
                        showValidation = validationRequested,
                        focusRequester = commonFocus.drugName,
                        rule = KyValidationRule.Required,
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyDrugRegistration) {
                                PharmTextField(value = state.draft.regNo, onValueChange = callbacks.onRegNo)
                            }
                        },
                        right = {
                            KyValidatedField(
                                label = s.commonUnit,
                                value = state.draft.unit,
                                onValueChange = callbacks.onUnit,
                                showValidation = validationRequested,
                                focusRequester = commonFocus.unit,
                                rule = KyValidationRule.Required,
                            )
                        },
                    )
                    KyTwoUp(
                        left = {
                            KyValidatedField(
                                label = s.commonQty,
                                value = state.draft.qty,
                                onValueChange = callbacks.onQty,
                                showValidation = validationRequested,
                                focusRequester = commonFocus.qty,
                                rule = KyValidationRule.PositiveInt,
                                keyboardType = KeyboardType.Number,
                            )
                        },
                        right = {
                            KyValidatedField(
                                label = s.kyHeaderPricePerUnit,
                                value = state.draft.pricePerUnit,
                                onValueChange = callbacks.onPricePerUnit,
                                showValidation = validationRequested,
                                focusRequester = priceFocus,
                                rule = KyValidationRule.NonNegativeNumber,
                                keyboardType = KeyboardType.Decimal,
                            )
                        },
                    )
                    FormField(label = pharmStrings.kySupplier) {
                        PharmTextField(value = state.draft.seller, onValueChange = callbacks.onSeller)
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeCommon(pharmStrings), onDismiss = callbacks.onDismissError)
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

@Preview
@Composable
private fun Ky9AddContent_Saving_Preview() {
    PharmacyTheme {
        Ky9AddContent(
            state = Ky9AddUiState(
                saving = true,
                draft = Ky9Draft(
                    date = "2026-06-01",
                    drugName = "Tramadol 50mg",
                    unit = "เม็ด",
                    qty = "100",
                    pricePerUnit = "2.50",
                ),
            ),
        )
    }
}
