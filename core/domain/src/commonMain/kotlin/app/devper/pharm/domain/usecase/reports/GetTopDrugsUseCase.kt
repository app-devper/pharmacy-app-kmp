package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.domain.param.reports.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.reports.ReportsRepository

class GetTopDrugsUseCase(private val repo: ReportsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<TopOrSlowDrugsParam, List<TopDrug>>(dispatchers) {
    override suspend fun execute(param: TopOrSlowDrugsParam): List<TopDrug> = repo.topDrugs(param)
    suspend operator fun invoke(days: Int = 30): Result<List<TopDrug>> = invoke(TopOrSlowDrugsParam(days))
}
