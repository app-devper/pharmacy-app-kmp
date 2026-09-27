package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.param.reports.CloseEodParam
import app.devper.pharm.domain.repository.profile.ProfileRepository
import app.devper.pharm.domain.repository.reports.ReportsRepository
import kotlinx.coroutines.CancellationException

/**
 * Closes a business day (ADR-0008). The signed-in user's display name is sent
 * for the close receipt; the backend records the verified user id either way,
 * so a failed profile lookup only leaves the name blank.
 */
class CloseEodUseCase(
    private val repo: ReportsRepository,
    private val profile: ProfileRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<CloseEodParam, EodCloseResult>(dispatchers) {
    override suspend fun execute(param: CloseEodParam): EodCloseResult {
        val name = try {
            profile.get().displayName
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ""
        }
        return repo.closeEod(param.copy(closedByName = name))
    }
}
