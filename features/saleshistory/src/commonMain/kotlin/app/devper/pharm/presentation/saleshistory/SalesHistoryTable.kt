package app.devper.pharm.presentation.saleshistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.format.localDateTimeToBuddhist
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SalesHistoryTable(
    sales: List<SaleSummary>,
    callbacks: SalesHistoryCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val s = pharmStrings
    val columns = remember(callbacks, s) {
        listOf(
        PharmTableColumn<SaleSummary>(
            header = s.salesHistoryHeaderTime,
            weight = 1.2f,
            cell = { sale -> TimeCell(sale) },
        ),
        PharmTableColumn(
            header = s.salesHistoryHeaderBillNo,
            weight = 1.4f,
            compactTitle = true,
            cell = { sale -> BillNoCell(sale) },
        ),
        PharmTableColumn(
            header = s.navCustomers,
            weight = 1.8f,
            cell = { sale ->
                Text(
                    text = sale.customerName.ifBlank { s.salesHistoryWalkInCustomer },
                    style = PharmText.bodySm,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = s.salesHistoryHeaderNet,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { sale -> TotalCell(sale) },
        ),
        PharmTableColumn(
            header = s.commonStatus,
            weight = 1.0f,
            cell = { sale -> StatusCell(sale) },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            compactTrailing = true,
            cell = { sale -> SalesRowActions(sale = sale, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = sales,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { sale -> callbacks.onOpenReceipt(sale) },
        rowHeight = 52.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = s.salesHistoryEmptySearching,
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.SalesHistory,
                    title = s.salesHistoryEmptyDateRange,
                )
            }
        },
    )
}

@Composable
private fun TimeCell(sale: SaleSummary) {
    val t = pharmTokens
    Text(
        text = localDateTimeToBuddhist(sale.soldAt),
        style = PharmText.micro.copy(
            color = t.colors.fg3,
            fontFeatureSettings = "tnum",
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun BillNoCell(sale: SaleSummary) {
    val t = pharmTokens
    Text(
        text = sale.billNo.ifBlank { pharmStrings.commonNoBillNo },
        style = PharmText.micro.copy(
            color = t.colors.fg2,
            fontFamily = FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun TotalCell(sale: SaleSummary) {
    val t = pharmTokens
    val style = PharmText.bodySm.copy(
        color = t.colors.accent,
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = "tnum",
        textDecoration = if (sale.voided) TextDecoration.LineThrough else null,
    )
    Text(text = fmtBaht(sale.total.amount), style = style)
}

@Composable
private fun StatusCell(sale: SaleSummary) {
    val s = pharmStrings
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (sale.voided) {
            PharmStatusBadge(status = PharmStatus.Voided)
        } else {
            PharmStatusBadge(status = PharmStatus.Done, label = s.salesHistoryStatusOk)
        }
    }
}

@Composable
private fun SalesRowActions(sale: SaleSummary, callbacks: SalesHistoryCallbacks) {
    val s = pharmStrings
    val actions = remember(sale.id, sale.voided, callbacks, s) {
        buildList {
            add(
                PharmAction(
                    label = s.salesHistoryActionViewBill,
                    icon = PharmIcons.SalesHistory,
                    tone = PharmActionTone.Primary,
                    onClick = { callbacks.onOpenReceipt(sale) },
                ),
            )
            if (!sale.voided) {
                add(
                    PharmAction(
                        label = s.salesHistoryActionReturn,
                        icon = PharmIcons.ReturnArrow,
                        tone = PharmActionTone.Danger,
                        onClick = { callbacks.onStartReturn(sale) },
                    ),
                )
            }
        }
    }
    PharmActionMenu(actions = actions)
}
