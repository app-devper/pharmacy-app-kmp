package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.reports.MovementsFilterParam
import app.devper.pharm.domain.repository.reports.MovementsRepository

class GetMovementsUseCase(private val repo: MovementsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<MovementsFilterParam, StockMovementsPage>(dispatchers) {
    override suspend fun execute(param: MovementsFilterParam): StockMovementsPage = repo.list(param)
}
