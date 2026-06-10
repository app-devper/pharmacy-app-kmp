package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.DeleteLotParam
import app.devper.pharm.domain.repository.LotsRepository

class DeleteLotUseCase(private val lots: LotsRepository, dispatchers: AppDispatchers) : BaseUseCase<DeleteLotParam, Unit>(dispatchers) {
    override suspend fun execute(param: DeleteLotParam) = lots.deleteLot(param)
}
