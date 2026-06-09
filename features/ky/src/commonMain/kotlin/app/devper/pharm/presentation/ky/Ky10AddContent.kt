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
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = pharmStrings.kyAddCtaWithNumber(10),
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
            PharmFormCard(title = pharmStrings.kyFormInfoSection(10)) {
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
                            FormField(label = pharmStrings.kyRemainingBalance) {
                                PharmTextField(
                                    value = state.draft.balance,
                                    onValueChange = callbacks.onBalance,
                                    keyboardType = KeyboardType.Number,
                                )
                            }
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
