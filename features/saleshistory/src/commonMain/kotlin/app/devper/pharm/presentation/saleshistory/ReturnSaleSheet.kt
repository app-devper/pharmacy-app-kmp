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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleItemSnapshot
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.PharmDivider
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmIconButton
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular

@Composable
fun ReturnSaleSheet(
    sale: SaleSummary,
    items: List<SaleItemSnapshot>,
    itemsLoading: Boolean,

    draft: Map<String, Int>,
    reason: String,
    submitting: Boolean,
    onLineQtyChange: (saleItemId: String, displayQty: Int) -> Unit,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val t = pharmTokens
    val anyDraft = draft.values.any { it > 0 }
    val canSubmit = anyDraft && reason.isNotBlank() && !submitting && !itemsLoading
    var validationRequested by rememberSaveable(sale.id) { mutableStateOf(false) }
    val reasonFocus = remember(sale.id) { FocusRequester() }
    val itemsError = validationRequested && !anyDraft
    val reasonError = validationRequested && reason.isBlank()
    val submitReturn: () -> Unit = {
        if (canSubmit) {
            onConfirm()
        } else if (!submitting && !itemsLoading) {
            validationRequested = true
            if (reason.isBlank()) reasonFocus.requestFocus()
            Unit
        }
    }

    PharmBottomSheet(
        onDismissRequest = onDismiss,
        dismissEnabled = !submitting,
    ) {
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

            PharmDivider(color = pharmTokens.colors.border)

            if (itemsLoading && items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
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
            }

            PharmDivider(color = pharmTokens.colors.border)

            if (itemsError) {
                Text(
                    text = pharmStrings.salesHistoryReturnItemsRequired,
                    style = PharmText.micro.copy(color = t.colors.dangerFg),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }

            FormField(
                label = pharmStrings.salesHistoryReasonLabel,
                required = true,
                error = if (reasonError) pharmStrings.salesHistoryReturnReasonRequired else null,
            ) {
                Box(modifier = Modifier.heightIn(min = 96.dp)) {
                    PharmTextField(
                        value = reason,
                        onValueChange = onReasonChange,
                        placeholder = pharmStrings.salesHistoryReturnReasonPlaceholder,
                        singleLine = false,
                        imeAction = ImeAction.Done,
                        onImeAction = submitReturn,
                        isError = reasonError,
                        focusRequester = reasonFocus,
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
                    onClick = submitReturn,
                    variant = PharmButtonVariant.Primary,
                    enabled = !submitting && !itemsLoading,
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
    val maxDisplay = item.returnableDisplayQty
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
            if (item.returnableQty < item.remainingQty) {
                Text(
                    text = pharmStrings.salesHistoryReturnCapHint(item.returnableDisplayQty, item.unreturnableQty),
                    style = PharmText.micro,
                    color = t.colors.warningFg,
                )
            }
            if (refund > 0) {
                Text(
                    text = pharmStrings.salesHistoryRefund(fmtBaht(refund)),
                    style = PharmText.meta.tabular(),
                    color = t.colors.accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(t.shapes.xl)
                .background(t.colors.surfaceRaised, t.shapes.xl),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(4.dp),
            ) {
                ReturnQuantityButton(
                    icon = PharmIcons.Minus,
                    contentDescription = pharmStrings.sellQtyDecrease,
                    onClick = { onChange((draftDisplay - 1).coerceAtLeast(0)) },
                    enabled = enabled && draftDisplay > 0,
                )
                Text(
                    text = draftDisplay.toString(),
                    style = PharmText.total.tabular(),
                    fontWeight = FontWeight.SemiBold,
                )
                ReturnQuantityButton(
                    icon = PharmIcons.Plus,
                    contentDescription = pharmStrings.sellQtyIncrease,
                    onClick = { onChange((draftDisplay + 1).coerceAtMost(maxDisplay)) },
                    enabled = enabled && draftDisplay < maxDisplay,
                )
            }
        }
    }
}

@Composable
private fun ReturnQuantityButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    PharmIconButton(
        contentDescription = contentDescription,
        enabled = enabled,
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) t.colors.fg1 else t.colors.fgMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}
