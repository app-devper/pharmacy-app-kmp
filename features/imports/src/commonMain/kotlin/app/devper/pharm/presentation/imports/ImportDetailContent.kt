package app.devper.pharm.presentation.imports

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.presentation.imports.i18n.localizeImports
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.components.SubPageBar
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.format.localDateTimeToBuddhist
import app.devper.pharm.ui.format.localDateToBuddhist
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ImportDetailContent(
    state: ImportDetailUiState,
    callbacks: ImportDetailCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        SubPageBar(
            title = state.po?.docNo ?: s.importsTitle,
            onBack = callbacks.onBack,
            actions = {
                state.po?.let { po ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(t.shapes.md)
                            .clickable(role = Role.Button, onClick = { callbacks.onEdit(po.id) }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(PharmIcons.Pencil, contentDescription = s.commonEdit, tint = t.colors.fg2, modifier = Modifier.size(20.dp))
                    }
                }
            },
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            when {
                state.loading && state.po == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress()
                }
                state.po == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(s.importsListEmpty, style = PharmText.body.copy(color = t.colors.fg2))
                }
                else -> Body(po = state.po)
            }
        }

        state.po?.let { po -> ImportDetailActionBar(po = po, state = state, callbacks = callbacks) }
    }

    if (state.confirmDialog) {
        PharmModal(
            open = true,
            onDismiss = callbacks.onCancelConfirm,
            title = s.importsConfirmReceiveTitle,
            footer = {
                PharmButton(
                    label = s.commonCancel,
                    onClick = callbacks.onCancelConfirm,
                    variant = PharmButtonVariant.Ghost,
                    enabled = !state.confirming,
                )
                PharmButton(
                    label = s.commonConfirm,
                    onClick = callbacks.onConfirmNow,
                    enabled = !state.confirming,
                    loading = state.confirming,
                )
            },
        ) {
            Text(s.importsConfirmReceiveMessage, style = PharmText.body)
        }
    }
    if (state.deleteDialog) {
        PharmModal(
            open = true,
            onDismiss = callbacks.onCancelDelete,
            title = s.importsConfirmDeleteReceivedTitle,
            footer = {
                PharmButton(
                    label = s.commonCancel,
                    onClick = callbacks.onCancelDelete,
                    variant = PharmButtonVariant.Ghost,
                    enabled = !state.deleting,
                )
                PharmButton(
                    label = s.commonDelete,
                    onClick = callbacks.onDeleteNow,
                    variant = PharmButtonVariant.Danger,
                    enabled = !state.deleting,
                    loading = state.deleting,
                )
            },
        ) {
            Text(s.importsConfirmDeleteDraftMessage, style = PharmText.body)
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeImports(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun Body(po: PurchaseOrder) {
    val t = pharmTokens
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item("header") { HeaderBlock(po) }
        item("header-divider") {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        }
        item("section") {
            Text(
                text = pharmStrings.importsFormItemListTitle(po.itemCount),
                style = PharmText.h3,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
            )
        }
        itemsIndexed(
            items = po.items,
            key = { _, item -> "${item.drugId}|${item.lotNumber}|${item.expiryDate?.toString()}" },
        ) { index, item ->
            ImportDetailItemRow(item)
            if (index < po.items.lastIndex) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            }
        }
    }
}

@Composable
private fun HeaderBlock(po: PurchaseOrder) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChip(po.status)
        }
        val s = pharmStrings
        Text(
            text = s.importsFormItemTotal(formatBahtCurrency(po.totalCost.amount)),
            style = PharmText.h2.tabular(),
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        DetailRow(s.importsFormSupplier, po.supplier.ifBlank { "-" })
        DetailRow(s.importsHeaderInvoiceNo, po.invoiceNo.ifBlank { "-" })
        DetailRow(s.importsFormReceiveDate, localDateToBuddhist(po.receiveDate).ifBlank { "-" })
        if (po.notes.isNotBlank()) DetailRow(s.commonNote, po.notes)
        DetailRow(s.importsFormCreatedAt, localDateTimeToBuddhist(po.createdAt))
        po.confirmedAt?.let {
            DetailRow(s.importsFormConfirmedAt, localDateTimeToBuddhist(it))
        }
    }
}

private val previewItems = listOf(
    PurchaseOrderItem(
        drugId = "d1",
        drugName = "พาราเซตามอล 500mg",
        lotNumber = "A12345",
        expiryDate = kotlinx.datetime.LocalDate.parse("2027-06-30"),
        qty = Quantity(100),
        costPrice = Money(1.25),
        sellPrice = Money(2.0),
    ),
    PurchaseOrderItem(
        drugId = "d2",
        drugName = "อะม็อกซีซิลลิน 250mg",
        lotNumber = "B67890",
        expiryDate = kotlinx.datetime.LocalDate.parse("2026-12-31"),
        qty = Quantity(50),
        costPrice = Money(3.5),
        sellPrice = null,
    ),
)

private fun previewPo(status: PurchaseOrderStatus) = PurchaseOrder(
    id = "po-1",
    docNo = "GR-2026-0001",
    supplier = "บริษัท เอ บี ซี ฟาร์มา",
    invoiceNo = "INV-001",
    receiveDate = kotlinx.datetime.LocalDate.parse("2026-06-05"),
    items = previewItems,
    itemCount = previewItems.size,
    totalCost = Money(300.0),
    status = status,
    notes = "รับของครบ",
    createdAt = kotlinx.datetime.LocalDateTime.parse("2026-06-05T09:30:00"),
    confirmedAt = if (status == PurchaseOrderStatus.Confirmed) kotlinx.datetime.LocalDateTime.parse("2026-06-05T10:00:00") else null,
)

@Preview
@Composable
private fun ImportDetailContent_Loaded_Preview() {
    PharmacyTheme {
        ImportDetailContent(
            state = ImportDetailUiState(po = previewPo(PurchaseOrderStatus.Draft)),
            callbacks = ImportDetailCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportDetailContent_Empty_Preview() {
    PharmacyTheme {
        ImportDetailContent(
            state = ImportDetailUiState(po = null),
            callbacks = ImportDetailCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportDetailContent_Confirmed_Preview() {
    PharmacyTheme {
        ImportDetailContent(
            state = ImportDetailUiState(po = previewPo(PurchaseOrderStatus.Confirmed)),
            callbacks = ImportDetailCallbacks(),
        )
    }
}
