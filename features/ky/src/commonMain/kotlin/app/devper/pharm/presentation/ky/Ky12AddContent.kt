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
import app.devper.pharm.ui.designsystem.pharmFormContentPadding
import app.devper.pharm.ui.designsystem.pharmFormContentWidth
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings
import androidx.compose.ui.tooling.preview.Preview

data class Ky12AddCallbacks(
    val onBack: () -> Unit = {},
    val onDate: (String) -> Unit = {},
    val onDrugName: (String) -> Unit = {},
    val onRegNo: (String) -> Unit = {},
    val onUnit: (String) -> Unit = {},
    val onQty: (String) -> Unit = {},
    val onTotalValue: (String) -> Unit = {},
    val onRxNo: (String) -> Unit = {},
    val onPatientName: (String) -> Unit = {},
    val onDoctor: (String) -> Unit = {},
    val onHospital: (String) -> Unit = {},
    val onStatus: (String) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

@Composable
fun Ky12AddContent(
    state: Ky12AddUiState,
    callbacks: Ky12AddCallbacks = Ky12AddCallbacks(),
) {
    val t = pharmTokens
    val s = pharmStrings
    var validationRequested by remember { mutableStateOf(false) }
    val commonFocus = rememberKyCommonFocusRequesters()
    val totalValueFocus = remember { FocusRequester() }
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = pharmStrings.kyAddCtaWithNumber(12),
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
                            totalValueFocus.requestFocus()
                        }
                    },
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .then(pharmFormContentWidth())
                .imePadding()
                .verticalScroll(rememberScrollState())
                .pharmFormContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = pharmStrings.kyFormInfoSection(12)) {
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
                                label = s.kyTotalValue,
                                value = state.draft.totalValue,
                                onValueChange = callbacks.onTotalValue,
                                showValidation = validationRequested,
                                focusRequester = totalValueFocus,
                                rule = KyValidationRule.OptionalNonNegativeNumber,
                                keyboardType = KeyboardType.Decimal,
                            )
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyPrescriptionNo) {
                                PharmTextField(value = state.draft.rxNo, onValueChange = callbacks.onRxNo)
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.kyPatientName) {
                                PharmTextField(value = state.draft.patientName, onValueChange = callbacks.onPatientName)
                            }
                        },
                    )
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyDoctorPrescriber) {
                                PharmTextField(value = state.draft.doctor, onValueChange = callbacks.onDoctor)
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.kyHospitalClinic) {
                                PharmTextField(value = state.draft.hospital, onValueChange = callbacks.onHospital)
                            }
                        },
                    )
                    FormField(label = pharmStrings.commonStatus) {
                        PharmTextField(
                            value = state.draft.status,
                            onValueChange = callbacks.onStatus,
                            placeholder = pharmStrings.kyHeaderStatusPlaceholder,
                        )
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeCommon(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Preview
@Composable
private fun Ky12AddContent_Empty_Preview() {
    PharmacyTheme {
        Ky12AddContent(state = Ky12AddUiState())
    }
}

@Preview
@Composable
private fun Ky12AddContent_Filled_Preview() {
    PharmacyTheme {
        Ky12AddContent(
            state = Ky12AddUiState(
                draft = Ky12Draft(
                    date = "2026-06-01",
                    drugName = "Morphine 10mg",
                    regNo = "1A 999/40",
                    qty = "10",
                    unit = "amp",
                    rxNo = "RX-9",
                    patientName = "นาย ก",
                    doctor = "นพ. ข",
                    hospital = "รพ. ค",
                    totalValue = "500",
                    status = "Dispensed",
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun Ky12AddContent_Saving_Preview() {
    PharmacyTheme {
        Ky12AddContent(
            state = Ky12AddUiState(
                saving = true,
                draft = Ky12Draft(
                    date = "2026-06-01",
                    drugName = "Morphine 10mg",
                    unit = "amp",
                    qty = "10",
                ),
            ),
        )
    }
}
