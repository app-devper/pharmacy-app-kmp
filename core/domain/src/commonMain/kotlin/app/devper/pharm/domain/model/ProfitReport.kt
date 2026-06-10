package app.devper.pharm.domain.model

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
