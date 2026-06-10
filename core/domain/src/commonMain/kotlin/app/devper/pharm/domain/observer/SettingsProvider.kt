package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsProvider(private val settings: SettingsRepository) {
    val state: StateFlow<Settings> get() = settings.settings
}
