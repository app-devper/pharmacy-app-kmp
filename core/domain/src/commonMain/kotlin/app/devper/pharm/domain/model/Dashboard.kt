package app.devper.pharm.domain.model

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
