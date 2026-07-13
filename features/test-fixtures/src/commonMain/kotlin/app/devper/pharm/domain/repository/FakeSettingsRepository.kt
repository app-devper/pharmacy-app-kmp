package app.devper.pharm.domain.repository

import app.devper.pharm.common.ServerException

import app.devper.pharm.domain.repository.settings.SettingsRepository

import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.param.settings.UpdateSettingsParam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository(
    initialSettings: Settings = Settings(),
    private val refreshThrows: Boolean = false,
    private val updateThrows: Boolean = false,
) : SettingsRepository {

    private val settingsState = MutableStateFlow(initialSettings)
    override val settings: StateFlow<Settings> = settingsState.asStateFlow()

    var refreshCallCount: Int = 0
        private set
    var lastUpdate: UpdateSettingsParam? = null
        private set

    override suspend fun refresh(): Settings {
        refreshCallCount++
        if (refreshThrows) throw ServerException("refresh failed")
        return settingsState.value
    }

    override suspend fun update(param: UpdateSettingsParam): Settings {
        if (updateThrows) throw ServerException("update failed")
        lastUpdate = param
        return settingsState.value
    }

    fun pushSettings(settings: Settings) {
        settingsState.value = settings
    }
}
