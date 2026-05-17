package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.repository.KyRepository

class GetKy12EntriesUseCase(private val repo: KyRepository, dispatchers: AppDispatchers) :
    BaseUseCase<KyMonthFilterParam, List<Ky12Entry>>(dispatchers) {
    override suspend fun execute(param: KyMonthFilterParam): List<Ky12Entry> = repo.listKy12(param)
    suspend operator fun invoke(): Result<List<Ky12Entry>> = invoke(KyMonthFilterParam())
}
