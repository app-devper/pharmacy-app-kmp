package app.devper.pharm.domain.usecase.settings

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.repository.UiPreferencesRepository

class SetThemePreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<ThemePreference, Unit>() {
    override fun execute(param: ThemePreference) = repo.setTheme(param)
}
