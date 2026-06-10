package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.AddKy9Param
import app.devper.pharm.domain.repository.KyRepository

class AddKy9UseCase(private val repo: KyRepository, dispatchers: AppDispatchers) : BaseUseCase<AddKy9Param, Unit>(dispatchers) {
    override suspend fun execute(param: AddKy9Param) = repo.addKy9(param)
}
