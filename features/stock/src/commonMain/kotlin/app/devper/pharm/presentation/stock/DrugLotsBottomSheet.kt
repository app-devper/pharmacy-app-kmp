package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.format.localDateToBuddhist
import app.devper.pharm.ui.i18n.pharmStrings

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
            callbacks.onClose()
            onDismiss()
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
                text = "ล็อต ${lot.lotNumber} จะถูกลบออก " +
                    "stock ของยาจะลดลง ${lot.remaining} หน่วย " +
                    "(การเคลื่อนไหวจะถูกบันทึกไว้)",
                style = PharmText.body,
            )
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
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
        modifier = Modifier.heightIn(max = 320.dp),
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
                state.lots.forEach { lot ->
                    LotRow(
                        lot = lot,
                        onDelete = { callbacks.onRequestDelete(lot) },
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        PharmButton(
            label = if (state.addFormOpen) pharmStrings.stockLotCloseAddForm else pharmStrings.stockLotAddCta,
            onClick = callbacks.onToggleAddForm,
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
private fun LotRow(lot: DrugLot, onDelete: () -> Unit) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lot.lotNumber,
                style = PharmText.body.copy(color = t.colors.fg1),
            )
            Text(
                text = "หมดอายุ ${localDateToBuddhist(lot.expiryDate)} · เหลือ ${lot.remaining}/${lot.quantity}",
                style = PharmText.micro.tabular().copy(color = t.colors.fg2),
            )
        }
        PharmButton(
            onClick = onDelete,
            variant = PharmButtonVariant.Ghost,
        ) {
            Icon(
                imageVector = PharmIcons.Trash,
                contentDescription = "ลบล็อต",
                tint = t.colors.dangerFg,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun AddLotForm(state: DrugLotsUiState, callbacks: DrugLotsCallbacks) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = pharmStrings.stockLotAddTitle,
            style = PharmText.h2,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LotField(
                label = pharmStrings.stockLotNumber,
                value = state.draft.lotNumber,
                onValueChange = callbacks.onLotNumber,
                placeholder = pharmStrings.stockLotNumberPlaceholder,
                modifier = Modifier.weight(1f),
            )
            LotField(
                label = pharmStrings.stockLotExpiryRequired,
                value = state.draft.expiryDate,
                onValueChange = callbacks.onExpiryDate,
                placeholder = "2026-12-31",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LotField(
                label = pharmStrings.stockLotInitialQty,
                value = state.draft.quantity,
                onValueChange = callbacks.onQuantity,
                placeholder = "0",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            LotField(
                label = pharmStrings.stockHeaderCostPrice,
                value = state.draft.costPrice,
                onValueChange = callbacks.onCostPrice,
                placeholder = pharmStrings.stockLotInitialCostHint,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
            LotField(
                label = pharmStrings.stockHeaderSellPrice,
                value = state.draft.sellPrice,
                onValueChange = callbacks.onSellPrice,
                placeholder = pharmStrings.stockLotInitialSellHint,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
        }
        PharmButton(
            label = "บันทึกล็อต",
            onClick = callbacks.onSubmitAdd,
            enabled = state.canSubmitDraft,
            loading = state.saving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LotField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    FormField(label = label, modifier = modifier) {
        PharmTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            keyboardType = keyboardType,
        )
    }
}
