package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository
import kotlinx.coroutines.flow.StateFlow

class UiPreferencesProvider(private val repo: UiPreferencesRepository) {
    val state: StateFlow<UiPreferences> get() = repo.state
}
