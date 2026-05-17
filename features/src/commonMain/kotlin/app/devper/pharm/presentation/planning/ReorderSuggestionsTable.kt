package app.devper.pharm.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import kotlin.math.roundToInt

@Composable
internal fun ReorderSuggestionsTable(
    suggestions: List<ReorderSuggestion>,
    callbacks: ReorderSuggestionsCallbacks,
    modifier: Modifier = Modifier,
) {
    val columns = remember(callbacks) {
        listOf(
        PharmTableColumn<ReorderSuggestion>(
            header = "ชื่อยา",
            weight = 2.4f,
            cell = { row -> SuggestionNameCell(row) },
        ),
        PharmTableColumn(
            header = "คงเหลือ",
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionStockCell(row) },
        ),
        PharmTableColumn(
            header = "แนะนำสั่ง",
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionSuggestedQtyCell(row) },
        ),
        PharmTableColumn(
            header = "ต้นทุนรวม",
            weight = 1.1f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionCostCell(row) },
        ),
        PharmTableColumn(
            header = "จัดการ",
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionRowActions(row = row, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = suggestions,
        columns = columns,
        key = { it.drugId },
        modifier = modifier,
        onRowClick = { callbacks.onRowClick(it) },
        rowHeight = 60.dp,
        emptyContent = {
            Text(text = "สต็อกเพียงพอกับยอดขาย", style = PharmText.meta)
        },
    )
}

@Composable
private fun SuggestionNameCell(row: ReorderSuggestion) {
    val t = pharmTokens
    val daysLeftText = if (row.isInfiniteDaysLeft) "—" else "${row.daysLeft.roundToInt()} วัน"
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = row.drugName,
            style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "ขายเฉลี่ย ${formatRate(row.avgDailySale)}/วัน · เหลือ $daysLeftText",
            style = PharmText.micro.copy(color = t.colors.fg3).tabular(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SuggestionStockCell(row: ReorderSuggestion) {
    val t = pharmTokens
    val isOut = row.currentStock <= 0
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.End,
    ) {
        Text(
            text = "${row.currentStock} ${row.unit}",
            style = PharmText.bodySm.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isOut) t.colors.dangerFg else t.colors.fg1,
            ).tabular(),
            maxLines = 1,
        )
        PharmStatusBadge(
            status = if (isOut) PharmStatus.OutOfStock else PharmStatus.LowStock,
            size = app.devper.pharm.ui.designsystem.PharmBadgeSize.Sm,
        )
    }
}

@Composable
private fun SuggestionSuggestedQtyCell(row: ReorderSuggestion) {
    val t = pharmTokens
    Text(
        text = "+${row.suggestedQty} ${row.unit}",
        style = PharmText.bodySm.copy(fontWeight = FontWeight.Bold, color = t.colors.accent).tabular(),
        maxLines = 1,
    )
}

@Composable
private fun SuggestionCostCell(row: ReorderSuggestion) {
    val t = pharmTokens
    val estimate = row.suggestedQty * row.costPrice
    if (row.costPrice <= 0.0) {
        Text(text = "—", style = PharmText.bodySm.copy(color = t.colors.fgMuted))
    } else {
        Text(
            text = fmtBaht(estimate),
            style = PharmText.bodySm.copy(color = t.colors.fg2).tabular(),
            maxLines = 1,
        )
    }
}

@Composable
private fun SuggestionRowActions(row: ReorderSuggestion, callbacks: ReorderSuggestionsCallbacks) {
    PharmActionMenu(
        actions = listOf(
            PharmAction(
                label = "เพิ่มใบสั่งซื้อ",
                icon = PharmIcons.Plus,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onAddToPurchaseOrder(row) },
            ),
            PharmAction(
                label = "ปิด",
                icon = PharmIcons.Close,
                onClick = { callbacks.onDismiss(row) },
            ),
        ),
    )
}

private fun formatRate(value: Double): String = when {
    value >= 1.0  -> ((value * 10).roundToInt() / 10.0).toString()
    value >= 0.1  -> ((value * 100).roundToInt() / 100.0).toString()
    else          -> ((value * 1000).roundToInt() / 1000.0).toString()
}
