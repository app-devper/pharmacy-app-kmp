package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.inventory.AddLotParam
import app.devper.pharm.domain.repository.inventory.LotsRepository

class AddLotUseCase(private val lots: LotsRepository, dispatchers: AppDispatchers) : BaseUseCase<AddLotParam, DrugLot>(dispatchers) {
    override suspend fun execute(param: AddLotParam): DrugLot = lots.addLot(param)
}
