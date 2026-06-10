package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.repository.LotsRepository

class ListLotsUseCase(private val lots: LotsRepository, dispatchers: AppDispatchers) : BaseUseCase<String, List<DrugLot>>(dispatchers) {
    override suspend fun execute(param: String): List<DrugLot> = lots.listLots(param)
}
