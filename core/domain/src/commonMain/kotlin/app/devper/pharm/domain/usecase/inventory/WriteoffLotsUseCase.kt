package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.WriteoffLotsParam
import app.devper.pharm.domain.repository.ExpiringLotsRepository

class WriteoffLotsUseCase(private val repo: ExpiringLotsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<WriteoffLotsParam, WriteoffResult>(dispatchers) {
    override suspend fun execute(param: WriteoffLotsParam): WriteoffResult = repo.writeoff(param)
}
