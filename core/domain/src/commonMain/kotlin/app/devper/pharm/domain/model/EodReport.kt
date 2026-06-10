package app.devper.pharm.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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
