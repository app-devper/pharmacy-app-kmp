package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.param.ExpiringLotsFilterParam
import app.devper.pharm.domain.repository.ExpiringLotsRepository

class GetExpiringLotsUseCase(private val repo: ExpiringLotsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<ExpiringLotsFilterParam, List<ExpiringLot>>(dispatchers) {
    override suspend fun execute(param: ExpiringLotsFilterParam): List<ExpiringLot> = repo.list(param)
}
