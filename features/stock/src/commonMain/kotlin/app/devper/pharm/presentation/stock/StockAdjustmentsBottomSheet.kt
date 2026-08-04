package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.presentation.stock.i18n.localizeStock
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.format.isoDateTimeToBuddhist
import app.devper.pharm.ui.i18n.label
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

data class StockAdjustmentsCallbacks(
    val onClose: () -> Unit = {},
    val onToggleAddForm: () -> Unit = {},
    val onSign: (AdjustmentSign) -> Unit = {},
    val onAbsDelta: (String) -> Unit = {},
    val onReason: (AdjustmentReason) -> Unit = {},
    val onNote: (String) -> Unit = {},
    val onSubmitAdd: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

@Composable
fun StockAdjustmentsBottomSheet(
    state: StockAdjustmentsUiState,
    callbacks: StockAdjustmentsCallbacks,
    onDismiss: () -> Unit,
) {
    if (state.drugId.isBlank()) return

    PharmBottomSheet(
        onDismissRequest = {
            callbacks.onClose()
            onDismiss()
        },
        dismissEnabled = !state.saving,
    ) {
        StockAdjustmentsContent(
            state = state,
            callbacks = callbacks,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        )
    }

    StockAdjustmentsDialogs(state = state, callbacks = callbacks)
}

@Composable
fun StockAdjustmentsContent(
    state: StockAdjustmentsUiState,
    callbacks: StockAdjustmentsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HeaderRow(state, callbacks)
        Divider()
        HistoryBody(state)
        if (state.addFormOpen) {
            Divider()
            AddAdjustmentForm(state, callbacks)
        }
    }
}

@Composable
fun StockAdjustmentsDialogs(
    state: StockAdjustmentsUiState,
    callbacks: StockAdjustmentsCallbacks,
) {
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
private fun HeaderRow(state: StockAdjustmentsUiState, callbacks: StockAdjustmentsCallbacks) {
    val t = pharmTokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pharmStrings.stockAdjustmentTitle,
                style = PharmText.h2,
            )
            Text(
                text = state.drugName,
                style = PharmText.body.copy(color = t.colors.fg2),
            )
        }
        PharmButton(
            label = if (state.addFormOpen) pharmStrings.commonClose else pharmStrings.stockNewAdjust,
            onClick = callbacks.onToggleAddForm,
            enabled = !state.saving,
            variant = PharmButtonVariant.Ghost,
            leadingIcon = {
                Icon(PharmIcons.Plus, contentDescription = null)
            },
        )
    }
}

@Composable
private fun HistoryBody(state: StockAdjustmentsUiState) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp, max = if (state.addFormOpen) 160.dp else 320.dp),
    ) {
        when {
            state.loading && state.history.isEmpty() -> Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center,
            ) { PharmCircularProgress() }

            state.history.isEmpty() -> Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    pharmStrings.stockHistoryEmpty,
                    style = PharmText.body.copy(color = t.colors.fg2),
                )
            }

            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(state.history, key = { it.id }) { row ->
                    AdjustmentRow(row)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun AdjustmentRow(adj: StockAdjustment) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ReasonBadge(adj.reason)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${adj.before} → ${adj.after}",
                style = PharmText.body.tabular().copy(color = t.colors.fg1),
            )
            Text(
                text = buildString {
                    append(isoDateTimeToBuddhist(adj.at))
                    if (adj.note.isNotBlank()) append("  ·  ${adj.note}")
                },
                style = PharmText.micro.tabular().copy(color = t.colors.fg2),
                maxLines = 2,
            )
        }
        Text(
            text = if (adj.delta > 0) "+${adj.delta}" else adj.delta.toString(),
            style = PharmText.h2.tabular().copy(
                color = if (adj.delta >= 0) t.colors.accent else t.colors.dangerFg,
            ),
        )
    }
}

@Composable
private fun ReasonBadge(reason: AdjustmentReason) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .clip(t.shapes.sm)
            .background(t.colors.borderSubtle, t.shapes.sm)
            .border(1.dp, t.colors.borderSubtle, t.shapes.sm)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = reason.label(pharmStrings),
            style = PharmText.micro.copy(color = t.colors.fg1),
        )
    }
}

@Composable
private fun AddAdjustmentForm(state: StockAdjustmentsUiState, callbacks: StockAdjustmentsCallbacks) {
    val strings = pharmStrings
    var validationRequested by rememberSaveable(state.addFormOpen) { mutableStateOf(false) }
    val quantityFocus = remember(state.addFormOpen) { FocusRequester() }
    val quantityError = if (validationRequested && !state.draft.absDeltaValid) {
        if (state.draft.absDelta.isBlank()) {
            strings.validationRequired(strings.fieldQuantity)
        } else {
            strings.validationMustBePositive(strings.fieldQuantity)
        }
    } else null
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = strings.stockNewAdjust,
            style = PharmText.h3,
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth < 360.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdjustmentSignChips(state, callbacks)
                    AdjustmentQuantityField(
                        state = state,
                        callbacks = callbacks,
                        error = quantityError,
                        focusRequester = quantityFocus,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AdjustmentSignChips(state, callbacks)
                    AdjustmentQuantityField(
                        state = state,
                        callbacks = callbacks,
                        error = quantityError,
                        focusRequester = quantityFocus,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        FormField(label = strings.stockHeaderReason) {
            PharmSingleSelectChips(
                chips = AdjustmentReason.pickerOrder.map {
                    PharmFilterChip(id = it.name, label = it.label(strings))
                },
                activeId = state.draft.reason.name,
                onSelect = { callbacks.onReason(AdjustmentReason.valueOf(it)) },
            )
        }

        FormField(label = strings.commonNote) {
            PharmTextField(
                value = state.draft.note,
                onValueChange = callbacks.onNote,
                placeholder = strings.commonNote,
                singleLine = false,
            )
        }

        PharmButton(
            label = strings.commonSave,
            onClick = {
                if (state.canSubmitDraft) {
                    callbacks.onSubmitAdd()
                } else {
                    validationRequested = true
                    quantityFocus.requestFocus()
                }
            },
            enabled = state.canAttemptSubmit,
            loading = state.saving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AdjustmentSignChips(
    state: StockAdjustmentsUiState,
    callbacks: StockAdjustmentsCallbacks,
) {
    PharmSingleSelectChips(
        chips = listOf(
            PharmFilterChip(id = AdjustmentSign.Decrease.name, label = pharmStrings.commonDelete),
            PharmFilterChip(id = AdjustmentSign.Increase.name, label = pharmStrings.commonAdd),
        ),
        activeId = state.draft.sign.name,
        onSelect = { callbacks.onSign(AdjustmentSign.valueOf(it)) },
        scrollable = false,
    )
}

@Composable
private fun AdjustmentQuantityField(
    state: StockAdjustmentsUiState,
    callbacks: StockAdjustmentsCallbacks,
    error: String?,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    FormField(
        label = pharmStrings.commonQty,
        error = error,
        modifier = modifier,
    ) {
        PharmTextField(
            value = state.draft.absDelta,
            onValueChange = callbacks.onAbsDelta,
            placeholder = pharmStrings.commonQty,
            keyboardType = KeyboardType.Number,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}
