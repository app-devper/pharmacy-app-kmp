package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.DrugRepository

class GetLowStockDrugsUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) : BaseQueryUseCase<List<Drug>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Drug> = drugs.lowStock()
}
