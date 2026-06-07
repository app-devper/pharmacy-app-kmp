package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.DailyDataDto
import app.devper.pharm.data.remote.dto.DashboardDto
import app.devper.pharm.data.remote.dto.DrugProfitDto
import app.devper.pharm.data.remote.dto.EodCloseResultDto
import app.devper.pharm.data.remote.dto.EodReportDto
import app.devper.pharm.data.remote.dto.MonthlyDataDto
import app.devper.pharm.data.remote.dto.ProfitReportDto
import app.devper.pharm.data.remote.dto.ProfitSummaryDto
import app.devper.pharm.data.remote.dto.ReportSummaryDto
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
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug

internal fun DashboardDto.toDomain(): Dashboard = Dashboard(
    summary = summary.toDomain(),
    daily = daily.map { it.toDomain() },
    monthly = monthly.map { it.toDomain() },
    recentSales = recentSales.map { it.toDomain() },
)

internal fun ReportSummaryDto.toDomain(): ReportSummary = ReportSummary(
    todaySales = todaySales,
    todayBills = todayBills,
    monthSales = monthSales,
    stockValue = stockValue,
    lowStock = lowStock,
    outStock = outStock,
)

internal fun DailyDataDto.toDomain(): DailySales = DailySales(day = day, total = total)

internal fun MonthlyDataDto.toDomain(): MonthlySales = MonthlySales(
    month = month,
    revenue = revenue,
    cost = cost,
    profit = profit,
)

internal fun TopDrugDto.toDomain(): TopDrug = TopDrug(
    drugId = drugId,
    drugName = drugName,
    qtySold = qtySold,
    revenue = revenue,
)

internal fun SlowDrugDto.toDomain(): SlowDrug = SlowDrug(
    drugId = drugId,
    drugName = drugName,
    stock = stock,
    unit = unit,
)

internal fun EodReportDto.toDomain(): EodReport = EodReport(
    date = date.parseLocalDateOrNull(),
    billCount = billCount,
    totalSales = totalSales,
    totalDiscount = totalDiscount,
    totalReceived = totalReceived,
    totalChange = totalChange,
    netCash = netCash,
    bills = bills.map { it.toDomain() },
)

internal fun EodCloseResultDto.toDomain(): EodCloseResult = EodCloseResult(
    closeId = closeId,
    date = date.parseLocalDateOrNull(),
    closedAt = closedAt.parseLocalDateTimeOrNull(),
    closedBy = closedBy,
    report = report.toDomain(),
)

internal fun ProfitReportDto.toDomain(): ProfitReport = ProfitReport(
    summary = summary.toDomain(),
    byDrug = byDrug.map { it.toDomain() },
)

internal fun ProfitSummaryDto.toDomain(): ProfitSummary = ProfitSummary(
    revenue = revenue,
    cost = cost,
    profit = profit,
    margin = margin,
    bills = bills,
)

internal fun DrugProfitDto.toDomain(): DrugProfit = DrugProfit(
    drugId = drugId,
    drugName = drugName,
    qtySold = qtySold,
    revenue = revenue,
    cost = cost,
    profit = profit,
    margin = margin,
)
