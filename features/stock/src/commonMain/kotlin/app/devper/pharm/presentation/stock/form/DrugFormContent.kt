package app.devper.pharm.presentation.stock.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.stock.AltUnitDraft
import app.devper.pharm.presentation.stock.DrugFormFields
import app.devper.pharm.presentation.stock.DrugFormMode
import app.devper.pharm.presentation.stock.DrugFormUiState
import app.devper.pharm.presentation.stock.i18n.localizeStock
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.components.SubPageBar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun DrugFormContent(
    state: DrugFormUiState,
    callbacks: DrugFormCallbacks,
) {
    val t = pharmTokens
    var validationRequested by remember(state.mode) { mutableStateOf(false) }
    val nameFocusRequester = remember { FocusRequester() }
    val costPriceFocusRequester = remember { FocusRequester() }
    val sellPriceFocusRequester = remember { FocusRequester() }
    val lotNumberFocusRequester = remember { FocusRequester() }
    val lotExpiryFocusRequester = remember { FocusRequester() }
    val lotCostPriceFocusRequester = remember { FocusRequester() }
    val lotSellPriceFocusRequester = remember { FocusRequester() }
    val tierPriceFocusRequesters = remember { TierPriceFocusRequesters() }
    val altUnitFocusRequesters = remember(state.form.altUnits.size) {
        List(state.form.altUnits.size) { AltUnitFocusRequesters() }
    }
    val firstInvalidAltUnitIndex = state.form.altUnits.indices.firstOrNull { index ->
        !state.form.altUnitNameValid(index) ||
            !state.form.altUnits[index].factorValid ||
            !state.form.altUnits[index].sellPriceValid
    }
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(
            title = if (state.mode is DrugFormMode.Edit) pharmStrings.stockFormTitleEdit else pharmStrings.stockFormTitleAdd,
            onBack = callbacks.onBack,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                    onInvalidSubmit = if (state.loading) null else {
                        {
                            validationRequested = true
                            when {
                                !state.form.nameValid -> nameFocusRequester.requestFocus()
                                !state.form.costPriceValid -> costPriceFocusRequester.requestFocus()
                                !state.form.sellPriceValid -> sellPriceFocusRequester.requestFocus()
                                !state.form.tierRetailValid -> tierPriceFocusRequesters.retail.requestFocus()
                                !state.form.tierRegularValid -> tierPriceFocusRequesters.regular.requestFocus()
                                !state.form.tierWholesaleValid -> tierPriceFocusRequesters.wholesale.requestFocus()
                                firstInvalidAltUnitIndex != null -> {
                                    val index = firstInvalidAltUnitIndex
                                    val unit = state.form.altUnits[index]
                                    val focus = altUnitFocusRequesters[index]
                                    when {
                                        !state.form.altUnitNameValid(index) -> focus.name.requestFocus()
                                        !unit.factorValid -> focus.factor.requestFocus()
                                        !unit.sellPriceValid -> focus.sellPrice.requestFocus()
                                    }
                                }
                                !state.form.initialLotNumberValid -> lotNumberFocusRequester.requestFocus()
                                !state.form.initialLotExpiryValid -> lotExpiryFocusRequester.requestFocus()
                                !state.form.initialLotCostPriceValid -> lotCostPriceFocusRequester.requestFocus()
                                !state.form.initialLotSellPriceValid -> lotSellPriceFocusRequester.requestFocus()
                            }
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
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                DrugFormDrugInfoSection(
                    form = state.form,
                    callbacks = callbacks,
                    showValidation = validationRequested,
                    nameFocusRequester = nameFocusRequester,
                    costPriceFocusRequester = costPriceFocusRequester,
                    sellPriceFocusRequester = sellPriceFocusRequester,
                )
                DrugFormPricingSections(
                    form = state.form,
                    callbacks = callbacks,
                    showValidation = validationRequested,
                    tierFocus = tierPriceFocusRequesters,
                    altUnitFocus = altUnitFocusRequesters,
                )
                when (val mode = state.mode) {
                    is DrugFormMode.Add ->
                        DrugFormInitialStockSection(
                            form = state.form,
                            callbacks = callbacks,
                            showValidation = validationRequested,
                            lotNumberFocusRequester = lotNumberFocusRequester,
                            lotExpiryFocusRequester = lotExpiryFocusRequester,
                            lotCostPriceFocusRequester = lotCostPriceFocusRequester,
                            lotSellPriceFocusRequester = lotSellPriceFocusRequester,
                        )
                    is DrugFormMode.Edit ->
                        DrugFormLotsAndAdjustmentsCard(
                            drugId = mode.drugId,
                            drugName = state.form.name,
                            onOpenLots = callbacks.onOpenLots,
                            onOpenAdjustments = callbacks.onOpenAdjustments,
                        )
                }
            }
        }
    }
    ErrorBottomSheet(message = state.errorState?.localizeStock(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun DrugFormLotsAndAdjustmentsCard(
    drugId: String,
    drugName: String,
    onOpenLots: (String, String) -> Unit,
    onOpenAdjustments: (String, String) -> Unit,
) {
    PharmFormCard(
        title = pharmStrings.stockLotsTitle,
        subtitle = pharmStrings.stockLotsSubtitle,
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmButton(
                label = pharmStrings.stockSeeAllLots,
                onClick = { onOpenLots(drugId, drugName) },
                variant = PharmButtonVariant.Outline,
            )
            PharmButton(
                label = pharmStrings.stockAdjustmentTitle,
                onClick = { onOpenAdjustments(drugId, drugName) },
                variant = PharmButtonVariant.Outline,
            )
        }
    }
}

@Preview
@Composable
private fun DrugFormContent_AddEmpty_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(),
            callbacks = DrugFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun DrugFormContent_AddFilled_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(
                form = DrugFormFields(
                    name = "Tylenol 500mg",
                    genericName = "Paracetamol",
                    strength = "500mg",
                    unit = "เม็ด",
                    type = pharmStrings.stockTypeRegular,
                    regNo = "1A 123/45",
                    barcode = "8851234567001",
                    costPrice = "1.2",
                    sellPrice = "2",
                    minStock = "20",
                    reportTypes = setOf("ky10", "ky11"),
                    initialStock = "200",
                    lotNumber = "PCM-260517",
                    lotExpiry = "2026-12-31",
                ),
            ),
            callbacks = DrugFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun DrugFormContent_Edit_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(
                mode = DrugFormMode.Edit("d1"),
                form = DrugFormFields(
                    name = "Paracetamol 500 mg",
                    genericName = "Paracetamol",
                    strength = "500 mg",
                    unit = "เม็ด",
                    type = pharmStrings.stockTypeRegular,
                    sellPrice = "2",
                    costPrice = "1",
                    altUnits = listOf(
                        AltUnitDraft(name = "แผง", factor = "10", sellPrice = "18"),
                    ),
                    reportTypes = setOf("ky9"),
                ),
            ),
            callbacks = DrugFormCallbacks(),
        )
    }
}

@Preview
@Composable
private fun DrugFormContent_Saving_Preview() {
    PharmacyTheme {
        DrugFormContent(
            state = DrugFormUiState(
                form = DrugFormFields(name = "X", sellPrice = "1"),
                saving = true,
            ),
            callbacks = DrugFormCallbacks(),
        )
    }
}
