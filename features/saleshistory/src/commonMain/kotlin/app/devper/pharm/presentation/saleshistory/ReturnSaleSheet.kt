package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.ui.i18n.pharmStrings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
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
    val t = pharmTokens
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
                text = pharmStrings.salesHistoryReturnTitle(sale.billNo),
                style = PharmText.h1,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = pharmStrings.salesHistoryReturnSubtitle,
                style = PharmText.bodySm,
                color = t.colors.fg2,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(pharmTokens.colors.border),
            )

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(pharmTokens.colors.border),
            )

            FormField(label = pharmStrings.salesHistoryReasonLabel, required = true) {
                Box(modifier = Modifier.heightIn(min = 96.dp)) {
                    PharmTextField(
                        value = reason,
                        onValueChange = onReasonChange,
                        placeholder = pharmStrings.salesHistoryReturnReasonPlaceholder,
                        singleLine = false,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                PharmButton(
                    label = pharmStrings.commonCancel,
                    onClick = onDismiss,
                    variant = PharmButtonVariant.Ghost,
                    enabled = !submitting,
                )
                PharmButton(
                    label = pharmStrings.salesHistoryReturnConfirmCta,
                    onClick = onConfirm,
                    variant = PharmButtonVariant.Primary,
                    enabled = canSubmit,
                    loading = submitting,
                )
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
    val t = pharmTokens
    val factor = if (item.unitFactor > 1) item.unitFactor else 1
    val draftDisplay = draftBaseQty / factor
    val maxDisplay = item.remainingDisplayQty
    val refund = item.price.amount * draftBaseQty

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.drugName,
                style = PharmText.body,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
            )
            Text(
                text = pharmStrings.salesHistorySoldRemaining(item.displayQty, item.displayUnit, item.remainingDisplayQty),
                style = PharmText.micro.tabular(),
                color = t.colors.fg2,
            )
            if (refund > 0) {
                Text(
                    text = pharmStrings.salesHistoryRefund(app.devper.pharm.ui.format.formatBahtCurrency(refund)),
                    style = PharmText.meta.tabular(),
                    color = t.colors.accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Surface(
            shape = t.shapes.xl,
            color = t.colors.surfaceRaised,
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
                        contentDescription = pharmStrings.sellQtyDecrease,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = draftDisplay.toString(),
                    style = PharmText.total.tabular(),
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(
                    onClick = { onChange((draftDisplay + 1).coerceAtMost(maxDisplay)) },
                    enabled = enabled && draftDisplay < maxDisplay,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = pharmStrings.sellQtyIncrease,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
