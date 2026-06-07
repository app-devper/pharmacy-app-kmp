package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class ReportSummary(
    val todaySales: Double,
    val todayBills: Int,
    val monthSales: Double,
    val stockValue: Double,
    val lowStock: Int,
    val outStock: Int,
)

data class DailySales(
    val day: String,
    val total: Double,
)

data class MonthlySales(
    val month: String,
    val revenue: Double,
    val cost: Double,
    val profit: Double,
)

data class TopDrug(
    val drugId: String,
    val drugName: String,
    val qtySold: Int,
    val revenue: Double,
)

data class SlowDrug(
    val drugId: String,
    val drugName: String,
    val stock: Int,
    val unit: String,
)

data class Dashboard(
    val summary: ReportSummary,
    val daily: List<DailySales>,
    val monthly: List<MonthlySales>,
    val recentSales: List<SaleSummary>,
)

data class EodReport(
    val date: LocalDate?,
    val billCount: Int,
    val totalSales: Double,
    val totalDiscount: Double,
    val totalReceived: Double,
    val totalChange: Double,
    val netCash: Double,
    val bills: List<SaleSummary>,
)

data class EodCloseResult(
    val closeId: String,
    val date: LocalDate?,
    val closedAt: LocalDateTime?,
    val closedBy: String,
    val report: EodReport,
)

data class ProfitReport(
    val summary: ProfitSummary,
    val byDrug: List<DrugProfit>,
)

data class ProfitSummary(
    val revenue: Double,
    val cost: Double,
    val profit: Double,
    val margin: Double,
    val bills: Int,
)

data class DrugProfit(
    val drugId: String,
    val drugName: String,
    val qtySold: Int,
    val revenue: Double,
    val cost: Double,
    val profit: Double,
    val margin: Double,
)
