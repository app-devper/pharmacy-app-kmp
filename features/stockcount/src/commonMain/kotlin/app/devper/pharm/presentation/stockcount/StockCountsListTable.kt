package app.devper.pharm.presentation.stockcount

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun StockCountsListTable(
    counts: List<StockCount>,
    callbacks: StockCountsListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val s = pharmStrings
    val columns = remember(callbacks, s) {
        listOf(
        PharmTableColumn<StockCount>(
            header = s.stockCountHeaderRound,
            weight = 1.6f,
            compactTitle = true,
            cell = { count -> StockCountNoCell(count) },
        ),
        PharmTableColumn(
            header = s.commonDate,
            weight = 1.4f,
            cell = { count -> StockCountDateCell(count) },
        ),
        PharmTableColumn(
            header = s.stockCountHeaderItems,
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { count -> StockCountItemsCell(count) },
        ),
        PharmTableColumn(
            header = s.stockCountHeaderAdjust,
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { count -> StockCountDeltaCell(count) },
        ),
        PharmTableColumn(
            header = s.stockCountHeaderNote,
            weight = 1.8f,
            hideInCompact = true,
            cell = { count -> StockCountNoteCell(count) },
        ),
        PharmTableColumn(
            header = s.commonStatus,
            weight = 0.9f,
            cell = { count -> StockCountStatusCell(count) },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { count -> StockCountRowActions(count = count, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = counts,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { callbacks.onOpenDetail(it) },
        rowHeight = 52.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = s.stockCountHistoryNotFound,
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.StockCount,
                    title = s.stockCountHistoryEmpty,
                )
            }
        },
    )
}

@Composable
private fun StockCountNoCell(count: StockCount) {
    val t = pharmTokens
    Text(
        text = count.countNo,
        style = PharmText.bodySm.copy(
            color = t.colors.fg1,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun StockCountDateCell(count: StockCount) {
    val t = pharmTokens
    Text(
        text = app.devper.pharm.ui.format.localDateTimeToBuddhist(count.createdAt),
        style = PharmText.meta.copy(color = t.colors.fg2),
        maxLines = 1,
    )
}

@Composable
private fun StockCountItemsCell(count: StockCount) {
    val t = pharmTokens
    Text(
        text = "${count.items.size}",
        style = PharmText.bodySm.copy(color = t.colors.fg1, fontWeight = FontWeight.Medium),
    )
}

@Composable
private fun StockCountDeltaCell(count: StockCount) {
    val t = pharmTokens
    val s = pharmStrings
    val totalAbs = count.items.sumOf { kotlin.math.abs(it.delta) }
    val changed = count.items.count { it.delta != 0 }
    val color = when {
        totalAbs == 0 -> t.colors.fgMuted
        else          -> t.colors.warningFg
    }
    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
        Text(
            text = if (totalAbs == 0) "0" else totalAbs.toString(),
            style = PharmText.bodySm.copy(color = color, fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = "$changed ${s.movementsCountNoun}",
            style = PharmText.micro.copy(color = t.colors.fgMuted),
        )
    }
}

@Composable
private fun StockCountNoteCell(count: StockCount) {
    val t = pharmTokens
    val note = count.note.takeIf { it.isNotBlank() }
    if (note == null) {
        Text(text = "—", style = PharmText.micro.copy(color = t.colors.fgMuted))
    } else {
        Text(
            text = note,
            style = PharmText.meta.copy(color = t.colors.fg2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StockCountStatusCell(count: StockCount) {
    val s = pharmStrings
    val anyChange = count.items.any { it.delta != 0 }
    val status = if (anyChange) PharmStatus.Done else PharmStatus.Draft
    val label = if (anyChange) s.stockCountStatusAdjusted else s.stockCountStatusNotAdjusted
    PharmStatusBadge(status = status, label = label, size = PharmBadgeSize.Sm)
}

@Composable
private fun StockCountRowActions(count: StockCount, callbacks: StockCountsListCallbacks) {
    val s = pharmStrings
    PharmActionMenu(
        actions = listOf(
            PharmAction(
                label = s.stockCountActionDetails,
                icon = PharmIcons.SalesHistory,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenDetail(count) },
            ),
            PharmAction(
                label = s.commonEdit,
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onEdit(count) },
            ),
            PharmAction(
                label = s.commonDelete,
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onDelete(count) },
            ),
        ),
    )
}
