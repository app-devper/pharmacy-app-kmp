package app.devper.pharm.presentation.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import kotlin.math.roundToInt

@Composable
internal fun ReorderSuggestionsTable(
    suggestions: List<ReorderSuggestion>,
    draftDrugIds: Set<String>,
    callbacks: ReorderSuggestionsCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val columns = remember(callbacks, s, draftDrugIds) {
        listOf(
        PharmTableColumn<ReorderSuggestion>(
            header = s.expiryHeaderDrugName,
            weight = 2.4f,
            cell = { row -> SuggestionNameCell(row, row.drugId in draftDrugIds) },
        ),
        PharmTableColumn(
            header = s.expiryHeaderRemaining,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionStockCell(row) },
        ),
        PharmTableColumn(
            header = s.planningHeaderRecommend,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionSuggestedQtyCell(row) },
        ),
        PharmTableColumn(
            header = s.planningHeaderTotalCost,
            weight = 1.1f,
            align = PharmColumnAlign.End,
            cell = { row -> SuggestionCostCell(row) },
        ),
        PharmTableColumn(
            header = s.customersHeaderActions,
            weight = 0.6f,
            align = PharmColumnAlign.End,
            compactTrailing = true,
            cell = { row ->
                SuggestionRowActions(
                    row = row,
                    added = row.drugId in draftDrugIds,
                    callbacks = callbacks,
                )
            },
        ),
        )
    }

    PharmTable(
        rows = suggestions,
        columns = columns,
        key = { it.drugId },
        modifier = modifier,
        rowHeight = 52.dp,
        emptyContent = {
            Text(text = s.planningReorderEmptyTitle, style = PharmText.meta)
        },
    )
}

@Composable
private fun SuggestionNameCell(row: ReorderSuggestion, added: Boolean) {
    val t = pharmTokens
    val s = pharmStrings
    val daysLeftText = if (row.isInfiniteDaysLeft) "—" else s.planningDaysLeftLabel(row.daysLeft.roundToInt())
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = row.drugName,
            style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = s.planningMetaLine(formatRate(row.avgDailySale), daysLeftText),
                style = PharmText.micro.copy(color = t.colors.fg3).tabular(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (added) {
                PharmBadge(
                    text = s.planningAddedBadge,
                    tone = PharmBadgeTone.Green,
                    size = PharmBadgeSize.Sm,
                )
            }
        }
    }
}

@Composable
private fun SuggestionStockCell(row: ReorderSuggestion) {
    val t = pharmTokens
    val isOut = !row.currentStock.isPositive
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.End,
    ) {
        Text(
            text = "${row.currentStock.value} ${row.unit}",
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
        text = "+${row.suggestedQty.value} ${row.unit}",
        style = PharmText.bodySm.copy(fontWeight = FontWeight.Bold, color = t.colors.accent).tabular(),
        maxLines = 1,
    )
}

@Composable
private fun SuggestionCostCell(row: ReorderSuggestion) {
    val t = pharmTokens
    val estimate = row.costPrice * row.suggestedQty.value
    if (!row.costPrice.isPositive) {
        Text(text = "—", style = PharmText.bodySm.copy(color = t.colors.fgMuted))
    } else {
        Text(
            text = fmtBaht(estimate.amount),
            style = PharmText.bodySm.copy(color = t.colors.fg2).tabular(),
            maxLines = 1,
        )
    }
}

@Composable
private fun SuggestionRowActions(
    row: ReorderSuggestion,
    added: Boolean,
    callbacks: ReorderSuggestionsCallbacks,
) {
    val s = pharmStrings
    PharmActionMenu(
        actions = listOf(
            PharmAction(
                label = if (added) s.planningAddedBadge else s.planningAddPoCta,
                icon = if (added) PharmIcons.Check else PharmIcons.Plus,
                tone = if (added) PharmActionTone.Success else PharmActionTone.Primary,
                enabled = !added,
                onClick = { callbacks.onAddToPurchaseOrder(row) },
            ),
            PharmAction(
                label = s.planningDismissCta,
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
