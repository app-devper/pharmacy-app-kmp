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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.localizeCommon
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.pharmFormContentPadding
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings
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
    val s = pharmStrings
    var validationRequested by remember { mutableStateOf(false) }
    val commonFocus = rememberKyCommonFocusRequesters()
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = pharmStrings.kyAddCtaWithNumber(11),
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                    onInvalidSubmit = {
                        validationRequested = true
                        commonFocus.requestFirstInvalid(
                            date = state.draft.date,
                            drugName = state.draft.drugName,
                            unit = state.draft.unit,
                            qty = state.draft.qty,
                        )
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
                .pharmFormContentPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PharmFormCard(title = pharmStrings.kyFormInfoSection(11)) {
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
                    KyValidatedField(
                        label = s.commonQty,
                        value = state.draft.qty,
                        onValueChange = callbacks.onQty,
                        showValidation = validationRequested,
                        focusRequester = commonFocus.qty,
                        rule = KyValidationRule.PositiveInt,
                        keyboardType = KeyboardType.Number,
                    )
                    FormField(label = pharmStrings.kyBuyerName) {
                        PharmTextField(value = state.draft.buyerName, onValueChange = callbacks.onBuyerName)
                    }
                    FormField(label = pharmStrings.kyPurposeOfUse) {
                        PharmTextField(
                            value = state.draft.purpose,
                            onValueChange = callbacks.onPurpose,
                            singleLine = false,
                        )
                    }
                    FormField(label = pharmStrings.kyPharmacistName) {
                        PharmTextField(value = state.draft.pharmacist, onValueChange = callbacks.onPharmacist)
                    }
                }
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeCommon(pharmStrings), onDismiss = callbacks.onDismissError)
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

@Preview
@Composable
private fun Ky11AddContent_Saving_Preview() {
    PharmacyTheme {
        Ky11AddContent(
            state = Ky11AddUiState(
                saving = true,
                draft = Ky11Draft(
                    date = "2026-06-01",
                    drugName = "Pseudoephedrine 60mg",
                    unit = "เม็ด",
                    qty = "20",
                ),
            ),
        )
    }
}
