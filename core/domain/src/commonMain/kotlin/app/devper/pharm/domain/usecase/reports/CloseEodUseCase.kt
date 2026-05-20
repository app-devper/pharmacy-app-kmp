package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.param.CloseEodParam
import app.devper.pharm.domain.repository.ReportsRepository

class CloseEodUseCase(private val repo: ReportsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<CloseEodParam, EodCloseResult>(dispatchers) {
    override suspend fun execute(param: CloseEodParam): EodCloseResult = repo.closeEod(param)
}
