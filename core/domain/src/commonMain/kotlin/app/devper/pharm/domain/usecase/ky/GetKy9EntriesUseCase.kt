package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.repository.KyRepository

class GetKy9EntriesUseCase(private val repo: KyRepository, dispatchers: AppDispatchers) :
    BaseUseCase<KyMonthFilterParam, List<Ky9Entry>>(dispatchers) {
    override suspend fun execute(param: KyMonthFilterParam): List<Ky9Entry> = repo.listKy9(param)
    suspend operator fun invoke(): Result<List<Ky9Entry>> = invoke(KyMonthFilterParam())
}
