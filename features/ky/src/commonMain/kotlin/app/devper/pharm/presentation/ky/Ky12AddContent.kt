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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.localizeCommon
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.components.SubPageBar
import app.devper.pharm.ui.designsystem.PharmSaveAction
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
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(
            title = pharmStrings.kyAddCtaWithNumber(12),
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = pharmStrings.kyFormInfoSection(12)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    KyTwoUp(
                        left = {
                            FormField(label = pharmStrings.kyDateYmd, required = true) {
                                PharmTextField(value = state.draft.date, onValueChange = callbacks.onDate)
                            }
                        },
                        right = {
                            FormField(label = pharmStrings.kyHeaderItem, required = true) {
                                PharmTextField(value = state.draft.drugName, onValueChange = callbacks.onDrugName)
                            }
                        },
                    )
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
                            FormField(label = pharmStrings.kyTotalValue) {
                                PharmTextField(
                                    value = state.draft.totalValue,
                                    onValueChange = callbacks.onTotalValue,
                                    keyboardType = KeyboardType.Decimal,
                                )
                            }
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
