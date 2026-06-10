package app.devper.pharm.domain.repository.reports

import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.domain.param.reports.CloseEodParam
import app.devper.pharm.domain.param.reports.DashboardRangeParam
import app.devper.pharm.domain.param.reports.EodReportParam
import app.devper.pharm.domain.param.reports.ReportRangeParam
import app.devper.pharm.domain.param.reports.TopOrSlowDrugsParam

interface ReportsRepository {

    suspend fun dashboard(param: DashboardRangeParam): Dashboard

    suspend fun topDrugs(param: TopOrSlowDrugsParam): List<TopDrug>

    suspend fun slowDrugs(param: TopOrSlowDrugsParam): List<SlowDrug>

    suspend fun profit(param: ReportRangeParam): ProfitReport

    suspend fun eod(param: EodReportParam): EodReport

    suspend fun closeEod(param: CloseEodParam): EodCloseResult
}
