package app.devper.pharm.presentation.reports

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import kotlin.math.roundToLong

@Composable
internal fun ProfitTable(
    rows: List<DrugProfit>,
    totals: ProfitTotals?,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val columns = remember(s) {
        listOf(
        PharmTableColumn<DrugProfit>(
            header = s.reportsHeaderDrugName,
            weight = 2.2f,
            compactTitle = true,
            cell = { row -> NameCell(row.drugName) },
        ),
        PharmTableColumn(
            header = s.reportsHeaderQtySold,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { row -> NumberCell(row.qtySold.toString()) },
        ),
        PharmTableColumn(
            header = s.reportsHeaderRevenue,
            weight = 1.2f,
            align = PharmColumnAlign.End,
            hideInCompact = true,
            cell = { row -> MoneyCell(row.revenue) },
        ),
        PharmTableColumn(
            header = s.reportsHeaderCost,
            weight = 1.2f,
            align = PharmColumnAlign.End,
            hideInCompact = true,
            cell = { row -> MoneyCell(row.cost, muted = true) },
        ),
        PharmTableColumn(
            header = s.reportsHeaderProfit,
            compactTrailing = true,
            weight = 1.2f,
            align = PharmColumnAlign.End,
            cell = { row -> MoneyCell(row.profit, bold = true) },
        ),
        PharmTableColumn(
            header = s.reportsCsvHeaderMargin,
            weight = 1.0f,
            align = PharmColumnAlign.End,
            cell = { row -> MarginCell(row.margin) },
        ),
        )
    }

    PharmTable(
        rows = rows,
        columns = columns,
        key = { it.drugId },
        modifier = modifier,
        emptyContent = {
            Text(text = pharmStrings.reportsSectionDailySalesEmpty, style = PharmText.meta)
        },
        bottomRow = totals?.let { { ProfitTotalsRow(columns = columns, totals = it) } },
    )
}

internal data class ProfitTotals(
    val qty: Int,
    val revenue: Double,
    val cost: Double,
    val profit: Double,
    val margin: Double,
)

@Composable
private fun ProfitTotalsRow(
    columns: List<PharmTableColumn<DrugProfit>>,
    totals: ProfitTotals,
) {
    val t = pharmTokens
    val cellRenderers: List<@Composable () -> Unit> = listOf(
        { Text(text = pharmStrings.reportsTotalLabel, style = PharmText.bodySm.copy(fontWeight = FontWeight.SemiBold)) },
        { TotalsNumber(totals.qty.toString()) },
        { TotalsMoney(totals.revenue) },
        { TotalsMoney(totals.cost, muted = true) },
        { TotalsMoney(totals.profit, accent = true) },
        { TotalsMargin(totals.margin) },
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(t.colors.border),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(t.colors.bgPage)
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEachIndexed { index, col ->
                Box(
                    modifier = Modifier.weight(col.weight),
                    contentAlignment = col.align.toBoxAlignment(),
                ) {
                    cellRenderers[index]()
                }
            }
        }
    }
}

@Composable
private fun NameCell(name: String) {
    val t = pharmTokens
    Text(
        text = name,
        style = PharmText.bodySm.copy(color = t.colors.fg1),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun NumberCell(value: String) {
    val t = pharmTokens
    Text(
        text = value,
        style = PharmText.bodySm.copy(
            color = t.colors.fg2,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun MoneyCell(amount: Double, muted: Boolean = false, bold: Boolean = false) {
    val t = pharmTokens
    val color = if (muted) t.colors.fg3 else t.colors.fg1
    val weight = if (bold) FontWeight.SemiBold else FontWeight.Normal
    Text(
        text = fmtBaht(amount),
        style = PharmText.bodySm.copy(
            color = color,
            fontWeight = weight,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun MarginCell(marginPct: Double) {
    val color = marginColor(marginPct)
    val rounded = (marginPct * 10).roundToLong() / 10.0
    Text(
        text = "$rounded%",
        style = PharmText.bodySm.copy(
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun TotalsNumber(value: String) {
    val t = pharmTokens
    Text(
        text = value,
        style = PharmText.bodySm.copy(
            color = t.colors.fg2,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun TotalsMoney(amount: Double, muted: Boolean = false, accent: Boolean = false) {
    val t = pharmTokens
    val color = when {
        accent -> t.colors.accent
        muted  -> t.colors.fg3
        else   -> t.colors.fg2
    }
    Text(
        text = fmtBaht(amount),
        style = PharmText.bodySm.copy(
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun TotalsMargin(marginPct: Double) {
    val color = marginColor(marginPct)
    val rounded = (marginPct * 10).roundToLong() / 10.0
    Text(
        text = "$rounded%",
        style = PharmText.bodySm.copy(
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontFeatureSettings = "tnum",
        ),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun marginColor(marginPct: Double): Color {
    val t = pharmTokens
    return when {
        marginPct >= 20.0 -> t.colors.successFg
        marginPct >= 0.0  -> t.colors.warningFg
        else              -> t.colors.dangerFg
    }
}

private fun PharmColumnAlign.toBoxAlignment(): Alignment = when (this) {
    PharmColumnAlign.Start  -> Alignment.CenterStart
    PharmColumnAlign.Center -> Alignment.Center
    PharmColumnAlign.End    -> Alignment.CenterEnd
}
