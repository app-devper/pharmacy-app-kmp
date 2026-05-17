package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.param.UpdateSettingsParam
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<Settings>

    suspend fun refresh(): Settings

    suspend fun update(param: UpdateSettingsParam): Settings
}
