package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.repository.KyRepository

class AddKy12UseCase(private val repo: KyRepository, dispatchers: AppDispatchers) : BaseUseCase<KyForm.Ky12, Unit>(dispatchers) {
    override suspend fun execute(param: KyForm.Ky12) = repo.submitKy12(param)
}
