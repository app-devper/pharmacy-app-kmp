package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.repository.SaleHistoryRepository

class GetSaleSummaryUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, SaleSummary>(dispatchers) {
    override suspend fun execute(param: String): SaleSummary = repo.get(param)
}
