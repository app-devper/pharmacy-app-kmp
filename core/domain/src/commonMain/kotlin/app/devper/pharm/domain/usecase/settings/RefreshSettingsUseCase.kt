package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.repository.SettingsRepository

class RefreshSettingsUseCase(private val repo: SettingsRepository, dispatchers: AppDispatchers) : BaseQueryUseCase<Settings>(dispatchers) {
    override suspend fun execute(param: Unit): Settings = repo.refresh()
}
