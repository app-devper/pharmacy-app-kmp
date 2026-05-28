package app.devper.pharm.presentation.stock

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentsBottomSheet(
    viewModel: StockAdjustmentsViewModel,
    onDismiss: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    if (state.drugId.isBlank()) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            viewModel.close()
            onDismiss()
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HeaderRow(state, viewModel)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            HistoryBody(state)
            if (state.addFormOpen) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                AddAdjustmentForm(state, viewModel)
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}

@Composable
private fun HeaderRow(state: StockAdjustmentsUiState, vm: StockAdjustmentsViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ปรับปรุงสต็อก",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = state.drugName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = vm::toggleAddForm) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Text(
                if (state.addFormOpen) "ปิด" else "ปรับปรุงใหม่",
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

@Composable
private fun HistoryBody(state: StockAdjustmentsUiState) {
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(state.history, key = { it.id }) { row ->
                    AdjustmentRow(row)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                }
            }
        }
    }
}

@Composable
private fun AdjustmentRow(adj: StockAdjustment) {
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
                style = MaterialTheme.typography.bodyMedium.tabular(),
            )
            Text(
                text = buildString {
                    append(adj.at.take(19).replace('T', ' '))
                    if (adj.note.isNotBlank()) append("  ·  ${adj.note}")
                },
                style = MaterialTheme.typography.labelSmall.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }
        Text(
            text = if (adj.delta > 0) "+${adj.delta}" else adj.delta.toString(),
            style = MaterialTheme.typography.titleMedium.tabular(),
            fontWeight = FontWeight.Bold,
            color = if (adj.delta >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun ReasonBadge(reason: AdjustmentReason) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = reason.wire,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAdjustmentForm(state: StockAdjustmentsUiState, vm: StockAdjustmentsViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "ปรับปรุงใหม่",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = state.draft.sign == AdjustmentSign.Decrease,
                    onClick = { vm.onSign(AdjustmentSign.Decrease) },
                    shape = SegmentedButtonDefaults.itemShape(0, 2),
                ) { Text("ลด") }
                SegmentedButton(
                    selected = state.draft.sign == AdjustmentSign.Increase,
                    onClick = { vm.onSign(AdjustmentSign.Increase) },
                    shape = SegmentedButtonDefaults.itemShape(1, 2),
                ) { Text("เพิ่ม") }
            }
            OutlinedTextField(
                value = state.draft.absDelta,
                onValueChange = vm::onAbsDelta,
                singleLine = true,
                placeholder = { Text("จำนวน") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier.weight(1f).height(56.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "เหตุผล",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdjustmentReason.pickerOrder.forEach { reason ->
                    FilterChip(
                        selected = state.draft.reason == reason,
                        onClick = { vm.onReason(reason) },
                        label = { Text(reason.wire) },
                        colors = FilterChipDefaults.filterChipColors(),
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.draft.note,
            onValueChange = vm::onNote,
            placeholder = { Text("หมายเหตุ (ออปชัน)") },
            singleLine = false,
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp, max = 96.dp),
        )

        Button(
            onClick = vm::submitAdd,
            enabled = state.canSubmitDraft,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            if (state.saving) {
                PharmCircularProgress(strokeWidth = 2.dp, modifier = Modifier.height(18.dp))
            } else {
                Text("บันทึก")
            }
        }
    }
}
