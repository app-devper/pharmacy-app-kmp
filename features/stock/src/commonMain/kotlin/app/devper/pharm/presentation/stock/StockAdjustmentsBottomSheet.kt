package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentsBottomSheet(
    state: StockAdjustmentsUiState,
    callbacks: StockAdjustmentsCallbacks,
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
private fun HeaderRow(state: StockAdjustmentsUiState, callbacks: StockAdjustmentsCallbacks) {
    val t = pharmTokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ปรับปรุงสต็อก",
                style = PharmText.h2,
            )
            Text(
                text = state.drugName,
                style = PharmText.body.copy(color = t.colors.fg2),
            )
        }
        PharmButton(
            label = if (state.addFormOpen) "ปิด" else "ปรับปรุงใหม่",
            onClick = callbacks.onToggleAddForm,
            variant = PharmButtonVariant.Ghost,
            leadingIcon = {
                Icon(PharmIcons.Plus, contentDescription = null, modifier = Modifier.size(18.dp))
            },
        )
    }
}

@Composable
private fun HistoryBody(state: StockAdjustmentsUiState) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 320.dp)) {
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
                    "ยังไม่มีประวัติการปรับปรุง",
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
                    append(adj.at.take(19).replace('T', ' '))
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
            text = reason.wire,
            style = PharmText.micro.copy(color = t.colors.fg1),
        )
    }
}

@Composable
private fun AddAdjustmentForm(state: StockAdjustmentsUiState, callbacks: StockAdjustmentsCallbacks) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "ปรับปรุงใหม่",
            style = PharmText.h3,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmSingleSelectChips(
                chips = listOf(
                    PharmFilterChip(id = AdjustmentSign.Decrease.name, label = "ลด"),
                    PharmFilterChip(id = AdjustmentSign.Increase.name, label = "เพิ่ม"),
                ),
                activeId = state.draft.sign.name,
                onSelect = { callbacks.onSign(AdjustmentSign.valueOf(it)) },
                scrollable = false,
            )
            FormField(label = "จำนวน", modifier = Modifier.weight(1f)) {
                PharmTextField(
                    value = state.draft.absDelta,
                    onValueChange = callbacks.onAbsDelta,
                    placeholder = "จำนวน",
                    keyboardType = KeyboardType.Number,
                )
            }
        }

        FormField(label = "เหตุผล") {
            PharmSingleSelectChips(
                chips = AdjustmentReason.pickerOrder.map {
                    PharmFilterChip(id = it.name, label = it.wire)
                },
                activeId = state.draft.reason.name,
                onSelect = { callbacks.onReason(AdjustmentReason.valueOf(it)) },
            )
        }

        FormField(label = "หมายเหตุ") {
            PharmTextField(
                value = state.draft.note,
                onValueChange = callbacks.onNote,
                placeholder = "หมายเหตุ (ออปชัน)",
                singleLine = false,
            )
        }

        PharmButton(
            label = "บันทึก",
            onClick = callbacks.onSubmitAdd,
            enabled = state.canSubmitDraft,
            loading = state.saving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
