package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.repository.ReportsRepository

class GetProfitReportUseCase(private val repo: ReportsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<ReportRangeParam, ProfitReport>(dispatchers) {
    override suspend fun execute(param: ReportRangeParam): ProfitReport = repo.profit(param)
}
