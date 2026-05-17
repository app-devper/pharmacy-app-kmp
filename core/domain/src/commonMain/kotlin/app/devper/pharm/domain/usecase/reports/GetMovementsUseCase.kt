package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.MovementsFilterParam
import app.devper.pharm.domain.repository.MovementsRepository

class GetMovementsUseCase(private val repo: MovementsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<MovementsFilterParam, StockMovementsPage>(dispatchers) {
    override suspend fun execute(param: MovementsFilterParam): StockMovementsPage = repo.list(param)
}
