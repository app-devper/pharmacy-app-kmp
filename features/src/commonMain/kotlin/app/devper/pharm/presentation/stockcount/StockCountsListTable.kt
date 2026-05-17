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
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun StockCountsListTable(
    counts: List<StockCount>,
    callbacks: StockCountsListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val columns = remember(callbacks) {
        listOf(
        PharmTableColumn<StockCount>(
            header = "เลขรอบ",
            weight = 1.6f,
            cell = { count -> StockCountNoCell(count) },
        ),
        PharmTableColumn(
            header = "วันที่",
            weight = 1.4f,
            cell = { count -> StockCountDateCell(count) },
        ),
        PharmTableColumn(
            header = "รายการ",
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { count -> StockCountItemsCell(count) },
        ),
        PharmTableColumn(
            header = "ปรับยอด",
            weight = 0.8f,
            align = PharmColumnAlign.End,
            cell = { count -> StockCountDeltaCell(count) },
        ),
        PharmTableColumn(
            header = "หมายเหตุ",
            weight = 1.8f,
            cell = { count -> StockCountNoteCell(count) },
        ),
        PharmTableColumn(
            header = "สถานะ",
            weight = 0.9f,
            cell = { count -> StockCountStatusCell(count) },
        ),
        PharmTableColumn(
            header = "จัดการ",
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
        rowHeight = 56.dp,
        emptyContent = {
            Text(
                text = if (emptySearching) "ไม่พบรอบนับตามที่ค้นหา" else "ยังไม่มีรอบนับสต็อก",
                style = PharmText.meta,
            )
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
        text = count.createdAt.take(19).replace('T', ' '),
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
            text = "$changed รายการ",
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
    val anyChange = count.items.any { it.delta != 0 }
    val status = if (anyChange) PharmStatus.Done else PharmStatus.Draft
    val label = if (anyChange) "ปรับแล้ว" else "ไม่ปรับ"
    PharmStatusBadge(status = status, label = label, size = PharmBadgeSize.Sm)
}

@Composable
private fun StockCountRowActions(count: StockCount, callbacks: StockCountsListCallbacks) {
    PharmActionMenu(
        actions = listOf(
            PharmAction(
                label = "ดูรายละเอียด",
                icon = PharmIcons.SalesHistory,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenDetail(count) },
            ),
            PharmAction(
                label = "แก้ไข",
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onEdit(count) },
            ),
            PharmAction(
                label = "ลบ",
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onDelete(count) },
            ),
        ),
    )
}
