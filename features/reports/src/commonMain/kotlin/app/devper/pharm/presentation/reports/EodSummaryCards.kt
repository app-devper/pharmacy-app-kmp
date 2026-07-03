package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.ui.designsystem.MetricCard
import app.devper.pharm.ui.designsystem.MetricCardRow
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.fmtBaht

@Composable
internal fun EodSummaryCards(report: EodReport, modifier: Modifier = Modifier) {
    val s = pharmStrings
    MetricCardRow(modifier = modifier) {
        MetricCard(
            label = s.reportsEodNetSalesLabel,
            value = fmtBaht(report.totalSales),
            sub = "${report.billCount} ${s.salesHistoryCountNoun}",
        )
        MetricCard(
            label = s.reportsEodCashReceived,
            value = fmtBaht(report.totalReceived),
            sub = s.reportsEodChannelSum,
        )
        MetricCard(
            label = s.reportsEodTotalDiscount,
            value = fmtBaht(report.totalDiscount),
            sub = s.reportsEodDayTotal,
        )
        MetricCard(
            label = s.reportsEodCashIn,
            value = fmtBaht(report.netCash),
            sub = s.reportsEodReceiveMinusChange,
        )
    }
}
