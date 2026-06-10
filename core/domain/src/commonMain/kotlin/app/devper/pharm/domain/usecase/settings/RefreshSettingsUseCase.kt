package app.devper.pharm.domain.usecase.settings

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.repository.settings.SettingsRepository

class RefreshSettingsUseCase(private val repo: SettingsRepository, dispatchers: AppDispatchers) : BaseQueryUseCase<Settings>(dispatchers) {
    override suspend fun execute(param: Unit): Settings = repo.refresh()
}
