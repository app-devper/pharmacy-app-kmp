package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.domain.param.DashboardRangeParam
import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.param.TopOrSlowDrugsParam

interface ReportsRepository {

    suspend fun dashboard(param: DashboardRangeParam): Dashboard

    suspend fun topDrugs(param: TopOrSlowDrugsParam): List<TopDrug>

    suspend fun slowDrugs(param: TopOrSlowDrugsParam): List<SlowDrug>

    suspend fun profit(param: ReportRangeParam): ProfitReport

    suspend fun eod(param: EodReportParam): EodReport
}
