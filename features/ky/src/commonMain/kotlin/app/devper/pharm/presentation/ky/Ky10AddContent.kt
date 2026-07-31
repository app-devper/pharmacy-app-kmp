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

data class Ky10AddCallbacks(
    val onBack: () -> Unit = {},
    val onDate: (String) -> Unit = {},
    val onDrugName: (String) -> Unit = {},
    val onRegNo: (String) -> Unit = {},
    val onUnit: (String) -> Unit = {},
    val onQty: (String) -> Unit = {},
    val onBalance: (String) -> Unit = {},
    val onBuyerName: (String) -> Unit = {},
    val onBuyerAddress: (String) -> Unit = {},
    val onRxNo: (String) -> Unit = {},
    val onDoctor: (String) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

@Composable
fun Ky10AddContent(
    state: Ky10AddUiState,
    callbacks: Ky10AddCallbacks = Ky10AddCallbacks(),
) {
    val t = pharmTokens
    val s = pharmStrings
    var validationRequested by remember { mutableStateOf(false) }
    val commonFocus = rememberKyCommonFocusRequesters()
    val balanceFocus = remember { FocusRequester() }
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = pharmStrings.kyAddCtaWithNumber(10),
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
                            balanceFocus.requestFocus()
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
            PharmFormCard(title = pharmStrings.kyFormInfoSection(10)) {
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
                            KyValidatedField(
                                label = s.kyHeaderItem,
                                value = state.draft.drugName,
                                onValueChange = callbacks.onDrugName,
                                showValidation = validationRequested,
                                focusRequester = commonFocus.drugName,
                                rule = KyValidationRule.Required,
                            )
                        },
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
                                label = s.kyRemainingBalance,
                                value = state.draft.balance,
                                onValueChange = callbacks.onBalance,
                                showValidation = validationRequested,
                                focusRequester = balanceFocus,
                                rule = KyValidationRule.OptionalNonNegativeInt,
                                keyboardType = KeyboardType.Number,
                            )
                        },
                    )
                    FormField(label = pharmStrings.kyBuyerName) {
                        PharmTextField(value = state.draft.buyerName, onValueChange = callbacks.onBuyerName)
                    }
                    FormField(label = pharmStrings.kyBuyerAddress) {
                        PharmTextField(
                            value = state.draft.buyerAddress,
                            onValueChange = callbacks.onBuyerAddress,
                            singleLine = false,
                        )
                    }
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyPrescriptionNo) {
                                PharmTextField(value = state.draft.rxNo, onValueChange = callbacks.onRxNo)
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.kyDoctorPrescriber) {
                                PharmTextField(value = state.draft.doctor, onValueChange = callbacks.onDoctor)
                            }
                        },
                    )
                }
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeCommon(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Preview
@Composable
private fun Ky10AddContent_Empty_Preview() {
    PharmacyTheme {
        Ky10AddContent(state = Ky10AddUiState())
    }
}

@Preview
@Composable
private fun Ky10AddContent_Filled_Preview() {
    PharmacyTheme {
        Ky10AddContent(
            state = Ky10AddUiState(
                draft = Ky10Draft(
                    date = "2026-06-01",
                    drugName = "Diazepam 5mg",
                    regNo = "1A 222/40",
                    qty = "30",
                    unit = "เม็ด",
                    buyerName = "นาย ก",
                    buyerAddress = "กทม.",
                    rxNo = "RX-1",
                    doctor = "นพ. ข",
                    balance = "120",
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun Ky10AddContent_Saving_Preview() {
    PharmacyTheme {
        Ky10AddContent(
            state = Ky10AddUiState(
                saving = true,
                draft = Ky10Draft(
                    date = "2026-06-01",
                    drugName = "Diazepam 5mg",
                    unit = "เม็ด",
                    qty = "30",
                ),
            ),
        )
    }
}
