package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.ReportsApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.ProfitReport
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
        api.topDrugs(param.days).map { it.toDomain() }

    override suspend fun slowDrugs(param: TopOrSlowDrugsParam): List<SlowDrug> =
        api.slowDrugs(param.days).map { it.toDomain() }

    override suspend fun profit(param: ReportRangeParam): ProfitReport =
        api.profit(param.from?.toIso().orEmpty(), param.to?.toIso().orEmpty()).toDomain()

    override suspend fun eod(param: EodReportParam): EodReport =
        api.eod(param.date?.toIso().orEmpty()).toDomain()

    override suspend fun closeEod(param: CloseEodParam): EodCloseResult =
        api.closeEod(param.date?.toIso().orEmpty()).toDomain()
}
