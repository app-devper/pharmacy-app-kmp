package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.param.KyMonthFilterParam
import app.devper.pharm.domain.repository.KyRepository

class GetKy10EntriesUseCase(private val repo: KyRepository, dispatchers: AppDispatchers) :
    BaseUseCase<KyMonthFilterParam, List<Ky10Entry>>(dispatchers) {
    override suspend fun execute(param: KyMonthFilterParam): List<Ky10Entry> = repo.listKy10(param)
    suspend operator fun invoke(): Result<List<Ky10Entry>> = invoke(KyMonthFilterParam())
}
