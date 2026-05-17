package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.repository.KyRepository

class GetKy11EntriesUseCase(private val repo: KyRepository, dispatchers: AppDispatchers) :
    BaseUseCase<KyMonthFilterParam, List<Ky11Entry>>(dispatchers) {
    override suspend fun execute(param: KyMonthFilterParam): List<Ky11Entry> = repo.listKy11(param)
    suspend operator fun invoke(): Result<List<Ky11Entry>> = invoke(KyMonthFilterParam())
}
