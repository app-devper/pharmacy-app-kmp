package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.param.reports.DashboardRangeParam
import app.devper.pharm.domain.repository.reports.ReportsRepository

class GetDashboardUseCase(private val repo: ReportsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<DashboardRangeParam, Dashboard>(dispatchers) {
    override suspend fun execute(param: DashboardRangeParam): Dashboard = repo.dashboard(param)
    suspend operator fun invoke(): Result<Dashboard> = invoke(DashboardRangeParam())
}
