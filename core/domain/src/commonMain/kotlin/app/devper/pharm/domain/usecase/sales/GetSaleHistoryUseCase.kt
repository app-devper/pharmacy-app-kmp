package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.SaleHistoryFilterParam
import app.devper.pharm.domain.repository.SaleHistoryRepository

class GetSaleHistoryUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SaleHistoryFilterParam, List<SaleSummary>>(dispatchers) {
    override suspend fun execute(param: SaleHistoryFilterParam): List<SaleSummary> = repo.list(param)
}
