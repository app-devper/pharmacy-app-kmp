package app.devper.pharm.presentation.stock

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.tabular
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugLotsBottomSheet(
    viewModel: DrugLotsViewModel,
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
            HeaderRow(state)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            LotsBody(state, viewModel)
            if (state.addFormOpen) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                AddLotForm(state, viewModel)
            }
        }
    }

    state.pendingDelete?.let { lot ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            title = { Text("ลบล็อตนี้?") },
            text = {
                Text(
                    "ล็อต ${lot.lotNumber} จะถูกลบออก " +
                        "stock ของยาจะลดลง ${lot.remaining} หน่วย " +
                        "(การเคลื่อนไหวจะถูกบันทึกไว้)",
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text("ลบ", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDelete) { Text("ยกเลิก") }
            },
        )
    }

    ErrorBottomSheet(message = state.error, onDismiss = viewModel::dismissError)
}

@Composable
private fun HeaderRow(state: DrugLotsUiState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ล็อตทั้งหมด",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            if (state.drugName.isNotBlank()) {
                Text(
                    text = state.drugName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LotsBody(state: DrugLotsUiState, viewModel: DrugLotsViewModel) {
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
                    text = "ยังไม่มีล็อต",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
            else -> {
                state.lots.forEach { lot ->
                    LotRow(
                        lot = lot,
                        onDelete = { viewModel.requestDelete(lot) },
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = viewModel::toggleAddForm) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = if (state.addFormOpen) "  ปิดฟอร์มเพิ่มล็อต" else "  เพิ่มล็อต",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun LotRow(lot: DrugLot, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lot.lotNumber,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "หมดอายุ ${lot.expiryDate.take(10)} · เหลือ ${lot.remaining}/${lot.quantity}",
                style = MaterialTheme.typography.labelSmall.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "ลบล็อต",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun AddLotForm(state: DrugLotsUiState, viewModel: DrugLotsViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "เพิ่มล็อตใหม่",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LotField(
                label = "เลขล็อต *",
                value = state.draft.lotNumber,
                onValueChange = viewModel::onLotNumber,
                placeholder = "เช่น L240501",
                modifier = Modifier.weight(1f),
            )
            LotField(
                label = "วันหมดอายุ * (YYYY-MM-DD)",
                value = state.draft.expiryDate,
                onValueChange = viewModel::onExpiryDate,
                placeholder = "2026-12-31",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LotField(
                label = "จำนวน *",
                value = state.draft.quantity,
                onValueChange = viewModel::onQuantity,
                placeholder = "0",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            LotField(
                label = "ราคาทุน/หน่วย",
                value = state.draft.costPrice,
                onValueChange = viewModel::onCostPrice,
                placeholder = "ใช้ราคาทุนของยา",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
            LotField(
                label = "ราคาขาย/หน่วย",
                value = state.draft.sellPrice,
                onValueChange = viewModel::onSellPrice,
                placeholder = "ใช้ราคาขายของยา",
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
        }
        Button(
            onClick = viewModel::submitAdd,
            enabled = state.canSubmitDraft,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            if (state.saving) {
                PharmCircularProgress(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )
            } else {
                Text("บันทึกล็อต", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LotField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text(placeholder) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        )
    }
}
