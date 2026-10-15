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
    val close: EodCloseInfo? = null,
    val adjustments: EodAdjustments? = null,
)

data class EodCloseInfo(
    val closeId: String,
    val closedAt: LocalDateTime?,
    val closedBy: String,
)

data class EodAdjustments(
    val count: Int,
    val adjustedBillCount: Int,
    val adjustedTotalSales: Double,
    val adjustedNetCash: Double,
)

data class EodCloseResult(
    val closeId: String,
    val date: LocalDate?,
    val closedAt: LocalDateTime?,
    val closedBy: String,
    val report: EodReport,
)
