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
    /** Set when the day has an End-of-day close; the figures above are then its snapshot. */
    val close: EodCloseInfo? = null,
    /** Late sale adjustments recorded after the close, if any. */
    val adjustments: EodAdjustments? = null,
)

data class EodCloseInfo(
    val closeId: String,
    val closedAt: LocalDateTime?,
    val closedBy: String,
)

/** A closed day's Late sale adjustments and its totals after them. */
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
