package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.domain.param.CloseEodParam
import app.devper.pharm.domain.param.DashboardRangeParam
import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.param.TopOrSlowDrugsParam

class FakeReportsRepository(
    private val eodResult: EodReport = EodReport(
        date = "2026-05-19",
        billCount = 0,
        totalSales = 0.0,
        totalDiscount = 0.0,
        totalReceived = 0.0,
        totalChange = 0.0,
        netCash = 0.0,
        bills = emptyList(),
    ),
    private val closeResult: EodCloseResult = EodCloseResult(
        closeId = "eod-2026-05-19",
        date = "2026-05-19",
        closedAt = "2026-05-19T23:59:00+07:00",
        closedBy = "tester",
        report = eodResult,
    ),
    private val eodThrows: Throwable? = null,
    private val closeThrows: Throwable? = null,
) : ReportsRepository {

    var lastEodParam: EodReportParam? = null
        private set
    var lastCloseParam: CloseEodParam? = null
        private set
    var closeCallCount: Int = 0
        private set

    override suspend fun dashboard(param: DashboardRangeParam): Dashboard = Dashboard(
        summary = ReportSummary(0.0, 0, 0.0, 0.0, 0, 0),
        daily = emptyList(),
        monthly = emptyList(),
        recentSales = emptyList(),
    )

    override suspend fun topDrugs(param: TopOrSlowDrugsParam): List<TopDrug> = emptyList()

    override suspend fun slowDrugs(param: TopOrSlowDrugsParam): List<SlowDrug> = emptyList()

    override suspend fun profit(param: ReportRangeParam): ProfitReport = ProfitReport(
        summary = ProfitSummary(0.0, 0.0, 0.0, 0.0, 0),
        byDrug = emptyList(),
    )

    override suspend fun eod(param: EodReportParam): EodReport {
        lastEodParam = param
        eodThrows?.let { throw it }
        return eodResult
    }

    override suspend fun closeEod(param: CloseEodParam): EodCloseResult {
        lastCloseParam = param
        closeCallCount++
        closeThrows?.let { throw it }
        return closeResult
    }
}
