package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.SettingsApi
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.param.settings.UpdateSettingsParam
import app.devper.pharm.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val api: SettingsApi,
) : SettingsRepository {

    private val _settings = MutableStateFlow(Settings())
    override val settings: StateFlow<Settings> = _settings.asStateFlow()

    override suspend fun refresh(): Settings {
        val dto = api.get()
        val mapped = dto.toDomain()
        _settings.value = mapped
        return mapped
    }

    override suspend fun update(param: UpdateSettingsParam): Settings {
        val dto = api.put(param.toDto())
        val mapped = dto.toDomain()
        _settings.value = mapped
        return mapped
    }
}
