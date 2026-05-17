package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.param.UpdateSettingsParam
import app.devper.pharm.domain.repository.SettingsRepository

class UpdateSettingsUseCase(private val repo: SettingsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<UpdateSettingsParam, Settings>(dispatchers) {
    override suspend fun execute(param: UpdateSettingsParam): Settings = repo.update(param)
}
