package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.api.ReportsApi
import app.devper.pharm.data.remote.dto.DailyDataDto
import app.devper.pharm.data.remote.dto.DashboardDto
import app.devper.pharm.data.remote.dto.DrugProfitDto
import app.devper.pharm.data.remote.dto.EodCloseResultDto
import app.devper.pharm.data.remote.dto.EodReportDto
import app.devper.pharm.data.remote.dto.MonthlyDataDto
import app.devper.pharm.data.remote.dto.ProfitReportDto
import app.devper.pharm.data.remote.dto.ProfitSummaryDto
import app.devper.pharm.data.remote.dto.ReportSummaryDto
import app.devper.pharm.data.remote.dto.SaleSummaryDto
import app.devper.pharm.data.remote.dto.SlowDrugDto
import app.devper.pharm.data.remote.dto.TopDrugDto
import app.devper.pharm.domain.model.DailySales
import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.MonthlySales
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.domain.param.CloseEodParam
import app.devper.pharm.domain.param.DashboardRangeParam
import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.param.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.ReportsRepository

class ReportsRepositoryImpl(private val api: ReportsApi) : ReportsRepository {

    override suspend fun dashboard(param: DashboardRangeParam): Dashboard =
        api.dashboard(param.days).toDomain()

    override suspend fun topDrugs(param: TopOrSlowDrugsParam): List<TopDrug> =
        api.topDrugs(param.days).map(::toDomain)

    override suspend fun slowDrugs(param: TopOrSlowDrugsParam): List<SlowDrug> =
        api.slowDrugs(param.days).map(::toDomain)

    override suspend fun profit(param: ReportRangeParam): ProfitReport =
        api.profit(param.from.trim(), param.to.trim()).toDomain()

    override suspend fun eod(param: EodReportParam): EodReport =
        api.eod(param.date.trim()).toDomain()

    override suspend fun closeEod(param: CloseEodParam): EodCloseResult =
        api.closeEod(param.date.trim()).toDomain()

    private fun DashboardDto.toDomain() = Dashboard(
        summary = summary.toDomain(),
        daily = daily.map(::toDomain),
        monthly = monthly.map(::toDomain),
        recentSales = recentSales.map(::toSaleSummary),
    )

    private fun ReportSummaryDto.toDomain() = ReportSummary(
        todaySales = todaySales,
        todayBills = todayBills,
        monthSales = monthSales,
        stockValue = stockValue,
        lowStock = lowStock,
        outStock = outStock,
    )

    private fun toDomain(d: DailyDataDto) = DailySales(day = d.day, total = d.total)

    private fun toDomain(d: MonthlyDataDto) = MonthlySales(
        month = d.month,
        revenue = d.revenue,
        cost = d.cost,
        profit = d.profit,
    )

    private fun toDomain(d: TopDrugDto) = TopDrug(
        drugId = d.drugId,
        drugName = d.drugName,
        qtySold = d.qtySold,
        revenue = d.revenue,
    )

    private fun toDomain(d: SlowDrugDto) = SlowDrug(
        drugId = d.drugId,
        drugName = d.drugName,
        stock = d.stock,
        unit = d.unit,
    )

    private fun toSaleSummary(d: SaleSummaryDto) = SaleSummary(
        id = d.id,
        billNo = d.billNo ?: "",
        customerName = d.customerName,
        total = d.total,
        discount = d.discount,
        soldAt = d.soldAt.parseLocalDateTimeOrNull(),
        voided = d.voided,
    )

    private fun EodReportDto.toDomain() = EodReport(
        date = date,
        billCount = billCount,
        totalSales = totalSales,
        totalDiscount = totalDiscount,
        totalReceived = totalReceived,
        totalChange = totalChange,
        netCash = netCash,
        bills = bills.map(::toSaleSummary),
    )

    private fun EodCloseResultDto.toDomain() = EodCloseResult(
        closeId = closeId,
        date = date,
        closedAt = closedAt,
        closedBy = closedBy,
        report = report.toDomain(),
    )

    private fun ProfitReportDto.toDomain() = ProfitReport(
        summary = summary.toDomain(),
        byDrug = byDrug.map(::toDomain),
    )

    private fun ProfitSummaryDto.toDomain() = ProfitSummary(
        revenue = revenue,
        cost = cost,
        profit = profit,
        margin = margin,
        bills = bills,
    )

    private fun toDomain(d: DrugProfitDto) = DrugProfit(
        drugId = d.drugId,
        drugName = d.drugName,
        qtySold = d.qtySold,
        revenue = d.revenue,
        cost = d.cost,
        profit = d.profit,
        margin = d.margin,
    )
}
