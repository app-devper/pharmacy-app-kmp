package app.devper.pharm.presentation.imports

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.format.localDateToBuddhist
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ImportsListTable(
    orders: List<PurchaseOrderSummary>,
    callbacks: ImportsListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val s = pharmStrings
    val columns = remember(callbacks, s) {
        listOf(
        PharmTableColumn<PurchaseOrderSummary>(
            header = s.commonDate,
            weight = 1.0f,
            cell = { row -> DateCell(row) },
        ),
        PharmTableColumn(
            header = s.importsHeaderDocNo,
            weight = 1.6f,
            compactTitle = true,
            cell = { row -> DocNoCell(row) },
        ),
        PharmTableColumn(
            header = s.importsHeaderSupplier,
            weight = 2.0f,
            cell = { row -> SupplierCell(row) },
        ),
        PharmTableColumn(
            header = s.importsHeaderInvoicePlaceholder,
            weight = 1.4f,
            cell = { row -> InvoiceCell(row) },
        ),
        PharmTableColumn(
            header = s.movementsCountNoun,
            weight = 0.7f,
            align = PharmColumnAlign.End,
            cell = { row -> ItemCountCell(row) },
        ),
        PharmTableColumn(
            header = s.importsHeaderTotal,
            weight = 1.1f,
            align = PharmColumnAlign.End,
            cell = { row -> TotalCell(row) },
        ),
        PharmTableColumn(
            header = s.commonStatus,
            weight = 0.9f,
            cell = { row -> StatusCell(row) },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { row -> ImportRowActions(row = row, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = orders,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { row -> callbacks.onOpenImport(row) },
        rowHeight = 56.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = s.importsListNotFound,
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.Imports,
                    title = s.importsListEmpty,
                )
            }
        },
    )
}

@Composable
private fun DateCell(row: PurchaseOrderSummary) {
    val t = pharmTokens
    Text(
        text = localDateToBuddhist(row.receiveDate),
        style = PharmText.micro.copy(
            color = t.colors.fg3,
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun DocNoCell(row: PurchaseOrderSummary) {
    val t = pharmTokens
    Text(
        text = row.docNo,
        style = PharmText.micro.copy(
            color = t.colors.fg2,
            fontFamily = FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun SupplierCell(row: PurchaseOrderSummary) {
    Text(
        text = row.supplier.ifBlank { "(ไม่ระบุผู้ขาย)" },
        style = PharmText.bodySm,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun InvoiceCell(row: PurchaseOrderSummary) {
    val t = pharmTokens
    Text(
        text = row.invoiceNo.ifBlank { "—" },
        style = PharmText.micro.copy(
            color = if (row.invoiceNo.isBlank()) t.colors.fgMuted else t.colors.fg3,
            fontFamily = if (row.invoiceNo.isBlank()) FontFamily.Default else FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ItemCountCell(row: PurchaseOrderSummary) {
    val t = pharmTokens
    Text(
        text = row.itemCount.toString(),
        style = PharmText.bodySm.copy(
            color = t.colors.fg2,
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
    )
}

@Composable
private fun TotalCell(row: PurchaseOrderSummary) {
    val t = pharmTokens
    Text(
        text = fmtBaht(row.totalCost.amount),
        style = PharmText.bodySm.copy(
            color = t.colors.fg1,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
    )
}

@Composable
private fun StatusCell(row: PurchaseOrderSummary) {
    val s = pharmStrings
    val status = when (row.status) {
        PurchaseOrderStatus.Draft     -> PharmStatus.Draft
        PurchaseOrderStatus.Confirmed -> PharmStatus.Confirmed
    }
    val label = when (row.status) {
        PurchaseOrderStatus.Draft     -> s.importsStatusDraft
        PurchaseOrderStatus.Confirmed -> s.importsStatusReceived
    }
    PharmStatusBadge(status = status, label = label)
}

@Composable
private fun ImportRowActions(row: PurchaseOrderSummary, callbacks: ImportsListCallbacks) {
    val s = pharmStrings
    val actions = when (row.status) {
        PurchaseOrderStatus.Draft -> listOf(
            PharmAction(
                label = s.importsActionConfirmReceive,
                icon = PharmIcons.Check,
                tone = PharmActionTone.Success,
                onClick = { callbacks.onRequestConfirm(row) },
            ),
            PharmAction(
                label = s.commonEdit,
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onEdit(row) },
            ),
            PharmAction(
                label = s.commonDelete,
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onRequestDelete(row) },
            ),
        )
        PurchaseOrderStatus.Confirmed -> listOf(
            PharmAction(
                label = s.importsActionView,
                icon = PharmIcons.Imports,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenImport(row) },
            ),
        )
    }
    PharmActionMenu(actions = actions)
}
