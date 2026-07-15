package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.presentation.stock.i18n.localizeStock
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmStamp
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.format.localDateToBuddhist
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

data class DrugLotsCallbacks(
    val onClose: () -> Unit = {},
    val onRequestDelete: (DrugLot) -> Unit = {},
    val onCancelDelete: () -> Unit = {},
    val onConfirmDelete: () -> Unit = {},
    val onToggleAddForm: () -> Unit = {},
    val onLotNumber: (String) -> Unit = {},
    val onExpiryDate: (String) -> Unit = {},
    val onQuantity: (String) -> Unit = {},
    val onCostPrice: (String) -> Unit = {},
    val onSellPrice: (String) -> Unit = {},
    val onSubmitAdd: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugLotsBottomSheet(
    state: DrugLotsUiState,
    callbacks: DrugLotsCallbacks,
    onDismiss: () -> Unit,
) {
    if (state.drugId.isBlank()) return

    val t = pharmTokens
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            if (!state.saving) {
                callbacks.onClose()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = t.colors.surface,
    ) {
        DrugLotsContent(
            state = state,
            callbacks = callbacks,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        )
    }

    DrugLotsDialogs(state = state, callbacks = callbacks)
}

@Composable
fun DrugLotsContent(
    state: DrugLotsUiState,
    callbacks: DrugLotsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HeaderRow(state)
        Divider()
        LotsBody(state, callbacks)
        if (state.addFormOpen) {
            Divider()
            AddLotForm(state, callbacks)
        }
    }
}

@Composable
fun DrugLotsDialogs(
    state: DrugLotsUiState,
    callbacks: DrugLotsCallbacks,
) {
    state.pendingDelete?.let { lot ->
        PharmModal(
            open = true,
            onDismiss = callbacks.onCancelDelete,
            title = pharmStrings.stockLotDeleteTitle,
            footer = {
                PharmButton(
                    label = pharmStrings.commonCancel,
                    onClick = callbacks.onCancelDelete,
                    variant = PharmButtonVariant.Ghost,
                )
                PharmButton(
                    label = pharmStrings.commonDelete,
                    onClick = callbacks.onConfirmDelete,
                    variant = PharmButtonVariant.Danger,
                )
            },
        ) {
            Text(
                text = pharmStrings.stockLotDeleteBody(lot.lotNumber, lot.remaining.value),
                style = PharmText.body,
            )
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeStock(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(pharmTokens.colors.divider),
    )
}

@Composable
private fun HeaderRow(state: DrugLotsUiState) {
    val t = pharmTokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pharmStrings.stockSeeAllLots,
                style = PharmText.h2,
            )
            if (state.drugName.isNotBlank()) {
                Text(
                    text = state.drugName,
                    style = PharmText.body.copy(color = t.colors.fg2),
                )
            }
        }
    }
}

@Composable
private fun LotsBody(state: DrugLotsUiState, callbacks: DrugLotsCallbacks) {
    val t = pharmTokens
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        when {
            state.loading && state.lots.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) { PharmCircularProgress() }
            }
            state.lots.isEmpty() -> {
                Text(
                    text = pharmStrings.stockLotsEmpty,
                    style = PharmText.body.copy(color = t.colors.fg2),
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (state.addFormOpen) 160.dp else 320.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(state.lots, key = { it.id }) { lot ->
                        LotRow(
                            lot = lot,
                            onDelete = { callbacks.onRequestDelete(lot) },
                            enabled = !state.saving,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        PharmButton(
            label = if (state.addFormOpen) pharmStrings.stockLotCloseAddForm else pharmStrings.stockLotAddCta,
            onClick = callbacks.onToggleAddForm,
            enabled = !state.saving,
            variant = PharmButtonVariant.Ghost,
            leadingIcon = {
                Icon(
                    imageVector = PharmIcons.Plus,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            },
        )
    }
}

@Composable
private fun LotRow(lot: DrugLot, onDelete: () -> Unit, enabled: Boolean) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PharmStamp(text = lot.lotNumber)
            Text(
                text = pharmStrings.stockLotExpiryRemaining(localDateToBuddhist(lot.expiryDate), lot.remaining.value, lot.quantity.value),
                style = PharmText.micro.tabular().copy(color = t.colors.fg2),
            )
        }
        PharmButton(
            onClick = onDelete,
            enabled = enabled,
            variant = PharmButtonVariant.Ghost,
        ) {
            Icon(
                imageVector = PharmIcons.Trash,
                contentDescription = pharmStrings.stockLotDeleteDesc,
                tint = t.colors.dangerFg,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun AddLotForm(state: DrugLotsUiState, callbacks: DrugLotsCallbacks) {
    val strings = pharmStrings
    var validationRequested by rememberSaveable(state.addFormOpen) { mutableStateOf(false) }
    val lotNumberFocus = remember(state.addFormOpen) { FocusRequester() }
    val expiryDateFocus = remember(state.addFormOpen) { FocusRequester() }
    val quantityFocus = remember(state.addFormOpen) { FocusRequester() }
    val costPriceFocus = remember(state.addFormOpen) { FocusRequester() }
    val sellPriceFocus = remember(state.addFormOpen) { FocusRequester() }
    val lotNumberError = if (validationRequested && !state.draft.lotNumberValid) {
        strings.validationRequired(strings.fieldLotNumber)
    } else null
    val expiryDateError = if (validationRequested && !state.draft.expiryDateValid) {
        if (state.draft.expiryDate.isBlank()) {
            strings.validationRequired(strings.fieldExpiryDate)
        } else {
            strings.validationInvalidDate(strings.fieldExpiryDate)
        }
    } else null
    val quantityError = if (validationRequested && !state.draft.quantityValid) {
        if (state.draft.quantity.isBlank()) {
            strings.validationRequired(strings.fieldQuantity)
        } else {
            strings.validationMustBePositive(strings.fieldQuantity)
        }
    } else null
    val costPriceError = if (validationRequested && !state.draft.costPriceValid) {
        strings.validationNotANumber(strings.stockHeaderCostPrice)
    } else null
    val sellPriceError = if (validationRequested && !state.draft.sellPriceValid) {
        strings.validationNotANumber(strings.stockHeaderSellPrice)
    } else null
    val fields = listOf(
        LotFieldSpec(
            label = strings.stockLotNumber,
            value = state.draft.lotNumber,
            onValueChange = callbacks.onLotNumber,
            placeholder = strings.stockLotNumberPlaceholder,
            required = true,
            error = lotNumberError,
            focusRequester = lotNumberFocus,
        ),
        LotFieldSpec(
            label = strings.stockLotExpiryRequired,
            value = state.draft.expiryDate,
            onValueChange = callbacks.onExpiryDate,
            placeholder = "2026-12-31",
            required = true,
            error = expiryDateError,
            focusRequester = expiryDateFocus,
        ),
        LotFieldSpec(
            label = strings.stockLotInitialQty,
            value = state.draft.quantity,
            onValueChange = callbacks.onQuantity,
            placeholder = "0",
            keyboardType = KeyboardType.Number,
            required = true,
            error = quantityError,
            focusRequester = quantityFocus,
        ),
        LotFieldSpec(
            label = strings.stockHeaderCostPrice,
            value = state.draft.costPrice,
            onValueChange = callbacks.onCostPrice,
            placeholder = strings.stockLotInitialCostHint,
            keyboardType = KeyboardType.Decimal,
            error = costPriceError,
            focusRequester = costPriceFocus,
        ),
        LotFieldSpec(
            label = strings.stockHeaderSellPrice,
            value = state.draft.sellPrice,
            onValueChange = callbacks.onSellPrice,
            placeholder = strings.stockLotInitialSellHint,
            keyboardType = KeyboardType.Decimal,
            error = sellPriceError,
            focusRequester = sellPriceFocus,
        ),
    )
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = strings.stockLotAddTitle,
            style = PharmText.h2,
        )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            when {
                maxWidth < 360.dp -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    fields.forEach { LotField(spec = it) }
                }
                maxWidth < 600.dp -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LotFieldRow(fields = fields.take(2))
                    LotFieldRow(fields = fields.slice(2..3))
                    LotField(spec = fields[4])
                }
                else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LotFieldRow(fields = fields.take(2))
                    LotFieldRow(fields = fields.drop(2))
                }
            }
        }
        PharmButton(
            label = strings.stockLotSaveCta,
            onClick = {
                if (state.draft.valid) {
                    callbacks.onSubmitAdd()
                } else {
                    validationRequested = true
                    when {
                        !state.draft.lotNumberValid -> lotNumberFocus.requestFocus()
                        !state.draft.expiryDateValid -> expiryDateFocus.requestFocus()
                        !state.draft.quantityValid -> quantityFocus.requestFocus()
                        !state.draft.costPriceValid -> costPriceFocus.requestFocus()
                        !state.draft.sellPriceValid -> sellPriceFocus.requestFocus()
                    }
                }
            },
            enabled = state.canAttemptSubmit,
            loading = state.saving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private data class LotFieldSpec(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val placeholder: String,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val required: Boolean = false,
    val error: String? = null,
    val focusRequester: FocusRequester,
)

@Composable
private fun LotFieldRow(fields: List<LotFieldSpec>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        fields.forEach { LotField(spec = it, modifier = Modifier.weight(1f)) }
    }
}

@Composable
private fun LotField(
    spec: LotFieldSpec,
    modifier: Modifier = Modifier,
) {
    FormField(
        label = spec.label,
        required = spec.required,
        error = spec.error,
        modifier = modifier,
    ) {
        PharmTextField(
            value = spec.value,
            onValueChange = spec.onValueChange,
            placeholder = spec.placeholder,
            keyboardType = spec.keyboardType,
            isError = spec.error != null,
            focusRequester = spec.focusRequester,
        )
    }
}
