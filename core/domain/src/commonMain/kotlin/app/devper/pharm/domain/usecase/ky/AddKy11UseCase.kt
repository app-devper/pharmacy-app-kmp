package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.repository.ky.KyRepository

class AddKy11UseCase(private val repo: KyRepository, dispatchers: AppDispatchers) : BaseUseCase<KyForm.Ky11, Unit>(dispatchers) {
    override suspend fun execute(param: KyForm.Ky11) = repo.submitKy11(param)
}
