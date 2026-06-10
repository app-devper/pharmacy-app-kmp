package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.param.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.ReportsRepository

class GetSlowDrugsUseCase(private val repo: ReportsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<TopOrSlowDrugsParam, List<SlowDrug>>(dispatchers) {
    override suspend fun execute(param: TopOrSlowDrugsParam): List<SlowDrug> = repo.slowDrugs(param)
    suspend operator fun invoke(days: Int = 90): Result<List<SlowDrug>> = invoke(TopOrSlowDrugsParam(days))
}
