package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.theme.fmtBaht

@Composable
internal fun StockMetricsRow(drugs: List<Drug>, modifier: Modifier = Modifier) {
    val total = drugs.size
    val oos = drugs.count { !it.stock.isPositive }
    val low = drugs.count { it.stock.isPositive && it.minStock.isPositive && it.stock <= it.minStock }
    val stockValue = drugs.sumOf { it.stock.value.coerceAtLeast(0) * it.costPrice.amount }

    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = "จำนวนรายการยา",
            value = total.toString(),
            sub = "รายการ",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "หมดสต็อก",
            value = oos.toString(),
            sub = "ต้องสั่งด่วน",
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "ใกล้หมด",
            value = low.toString(),
            sub = "ต่ำกว่าขั้นต่ำ",
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "มูลค่าสต็อก",
            value = fmtBaht(stockValue),
            sub = "ตามราคาทุน",
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
    }
}
