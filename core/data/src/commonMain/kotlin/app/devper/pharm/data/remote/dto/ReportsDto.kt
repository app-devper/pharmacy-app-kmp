package app.devper.pharm.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportSummaryDto(
    @SerialName("today_sales") val todaySales: Double = 0.0,
    @SerialName("today_bills") val todayBills: Int = 0,
    @SerialName("month_sales") val monthSales: Double = 0.0,
    @SerialName("stock_value") val stockValue: Double = 0.0,
    @SerialName("low_stock") val lowStock: Int = 0,
    @SerialName("out_stock") val outStock: Int = 0,
)

@Serializable
data class DailyDataDto(
    @SerialName("day") val day: String = "",
    @SerialName("total") val total: Double = 0.0,
)

@Serializable
data class MonthlyDataDto(
    @SerialName("month") val month: String = "",
    @SerialName("revenue") val revenue: Double = 0.0,
    @SerialName("cost") val cost: Double = 0.0,
    @SerialName("profit") val profit: Double = 0.0,
)

@Serializable
data class TopDrugDto(
    @SerialName("drug_id") val drugId: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("qty_sold") val qtySold: Int = 0,
    @SerialName("revenue") val revenue: Double = 0.0,
)

@Serializable
data class SlowDrugDto(
    @SerialName("drug_id") val drugId: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("stock") val stock: Int = 0,
    @SerialName("unit") val unit: String = "",
)

@Serializable
data class DashboardDto(
    @SerialName("summary") val summary: ReportSummaryDto = ReportSummaryDto(),
    @SerialName("daily") val daily: List<DailyDataDto> = emptyList(),
    @SerialName("monthly") val monthly: List<MonthlyDataDto> = emptyList(),
    @SerialName("recent_sales") val recentSales: List<SaleSummaryDto> = emptyList(),
)

@Serializable
data class EodReportDto(
    @SerialName("date") val date: String = "",
    @SerialName("bill_count") val billCount: Int = 0,
    @SerialName("total_sales") val totalSales: Double = 0.0,
    @SerialName("total_discount") val totalDiscount: Double = 0.0,
    @SerialName("total_received") val totalReceived: Double = 0.0,
    @SerialName("total_change") val totalChange: Double = 0.0,
    @SerialName("net_cash") val netCash: Double = 0.0,
    @SerialName("bills") val bills: List<SaleSummaryDto> = emptyList(),
)

@Serializable
data class CloseEodRequestDto(
    @SerialName("date") val date: String = "",
)

@Serializable
data class EodCloseResultDto(
    @SerialName("close_id") val closeId: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("closed_at") val closedAt: String = "",
    @SerialName("closed_by") val closedBy: String = "",
    @SerialName("report") val report: EodReportDto = EodReportDto(),
)

@Serializable
data class ProfitReportDto(
    @SerialName("summary") val summary: ProfitSummaryDto = ProfitSummaryDto(),
    @SerialName("by_drug") val byDrug: List<DrugProfitDto> = emptyList(),
)

@Serializable
data class ProfitSummaryDto(
    @SerialName("revenue") val revenue: Double = 0.0,
    @SerialName("cost") val cost: Double = 0.0,
    @SerialName("profit") val profit: Double = 0.0,
    @SerialName("margin") val margin: Double = 0.0,
    @SerialName("bills") val bills: Int = 0,
)

@Serializable
data class DrugProfitDto(
    @SerialName("drug_id") val drugId: String = "",
    @SerialName("drug_name") val drugName: String = "",
    @SerialName("qty_sold") val qtySold: Int = 0,
    @SerialName("revenue") val revenue: Double = 0.0,
    @SerialName("cost") val cost: Double = 0.0,
    @SerialName("profit") val profit: Double = 0.0,
    @SerialName("margin") val margin: Double = 0.0,
)
