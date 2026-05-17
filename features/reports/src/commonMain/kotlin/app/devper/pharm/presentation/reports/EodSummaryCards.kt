package app.devper.pharm.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricTint
import app.devper.pharm.ui.theme.fmtBaht

@Composable
internal fun EodSummaryCards(report: EodReport, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MetricCard(
            label = "ยอดขายสุทธิ",
            value = fmtBaht(report.totalSales),
            sub = "${report.billCount} บิล",
            tint = MetricTint.Blue,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "รับเงิน",
            value = fmtBaht(report.totalReceived),
            sub = "รวมทุกช่องทาง",
            tint = MetricTint.Indigo,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "ส่วนลดรวม",
            value = fmtBaht(report.totalDiscount),
            sub = "รวมทั้งวัน",
            tint = MetricTint.Purple,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "เงินเข้าลิ้นชัก",
            value = fmtBaht(report.netCash),
            sub = "รับ − ทอน",
            tint = MetricTint.Green,
            modifier = Modifier.weight(1f),
        )
    }
}
