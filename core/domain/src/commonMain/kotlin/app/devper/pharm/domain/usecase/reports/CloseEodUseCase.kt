package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.observer.SignedInUser
import app.devper.pharm.domain.param.reports.CloseEodParam
import app.devper.pharm.domain.repository.reports.ReportsRepository

class CloseEodUseCase(
    private val repo: ReportsRepository,
    private val signedInUser: SignedInUser,
    dispatchers: AppDispatchers,
) : BaseUseCase<CloseEodParam, EodCloseResult>(dispatchers) {
    override suspend fun execute(param: CloseEodParam): EodCloseResult =
        repo.closeEod(param.copy(closedByName = signedInUser.displayNameOrBlank()))
}
