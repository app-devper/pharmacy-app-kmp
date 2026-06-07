package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun StockMetricsRow(drugs: List<Drug>, modifier: Modifier = Modifier) {
    val total = drugs.size
    val oos = drugs.count { !it.stock.isPositive }
    val low = drugs.count { it.stock.isPositive && it.minStock.isPositive && it.stock <= it.minStock }
    val stockValue = drugs.sumOf { it.stock.value.coerceAtLeast(0) * it.costPrice.amount }

    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = pharmStrings.stockMetricCount,
            value = total.toString(),
            sub = pharmStrings.movementsCountNoun,
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = pharmStrings.stockMetricOut,
            value = oos.toString(),
            sub = pharmStrings.stockStatusUrgent,
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = pharmStrings.stockStatusLow,
            value = low.toString(),
            sub = pharmStrings.stockMetricBelowMin,
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = pharmStrings.reportsMetricStockValue,
            value = fmtBaht(stockValue),
            sub = pharmStrings.stockMetricValueByCost,
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
    }
}
