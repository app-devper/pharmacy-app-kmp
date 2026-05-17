package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.theme.tabular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnSaleSheet(
    sale: SaleSummary,
    items: List<SaleItemSnapshot>,

    draft: Map<String, Int>,
    reason: String,
    submitting: Boolean,
    onLineQtyChange: (saleItemId: String, displayQty: Int) -> Unit,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val anyDraft = draft.values.any { it > 0 }
    val canSubmit = anyDraft && reason.isNotBlank() && !submitting

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "คืนสินค้าจากบิล ${sale.billNo}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "เลือกจำนวนที่จะคืน — ระบบจะคำนวณยอดคืนให้เอง",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items, key = { it.id }) { item ->
                    ReturnLineRow(
                        item = item,
                        draftBaseQty = draft[item.id] ?: 0,
                        enabled = !submitting,
                        onChange = { onLineQtyChange(item.id, it) },
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            OutlinedTextField(
                value = reason,
                onValueChange = onReasonChange,
                placeholder = { Text("เหตุผลการคืน เช่น ลูกค้าเปลี่ยนใจ, สินค้าเสีย ฯลฯ") },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth().height(96.dp),
                shape = MaterialTheme.shapes.medium,
            )

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(onClick = onDismiss, enabled = !submitting) {
                    Text("ยกเลิก", style = MaterialTheme.typography.titleMedium)
                }
                Button(onClick = onConfirm, enabled = canSubmit) {
                    if (submitting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                        )
                    } else {
                        Text("ยืนยันคืนสินค้า", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReturnLineRow(
    item: SaleItemSnapshot,
    draftBaseQty: Int,
    enabled: Boolean,
    onChange: (displayQty: Int) -> Unit,
) {
    val factor = if (item.unitFactor > 1) item.unitFactor else 1
    val draftDisplay = draftBaseQty / factor
    val maxDisplay = item.remainingDisplayQty
    val refund = item.price * draftBaseQty

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.drugName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
            )
            Text(
                text = "ขายไป ${item.displayQty} ${item.displayUnit} · เหลือคืน ${item.remainingDisplayQty}",
                style = MaterialTheme.typography.labelSmall.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (refund > 0) {
                Text(
                    text = "คืนเงิน ${app.devper.pharm.ui.format.formatBahtCurrency(refund)}",
                    style = MaterialTheme.typography.labelMedium.tabular(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(4.dp),
            ) {
                IconButton(
                    onClick = { onChange((draftDisplay - 1).coerceAtLeast(0)) },
                    enabled = enabled && draftDisplay > 0,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = "ลด",
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = draftDisplay.toString(),
                    style = MaterialTheme.typography.titleMedium.tabular(),
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(
                    onClick = { onChange((draftDisplay + 1).coerceAtMost(maxDisplay)) },
                    enabled = enabled && draftDisplay < maxDisplay,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "เพิ่ม",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
