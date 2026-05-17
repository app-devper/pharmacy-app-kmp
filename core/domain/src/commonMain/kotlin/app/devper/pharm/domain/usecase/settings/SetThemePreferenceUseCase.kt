package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.repository.UiPreferencesRepository

class SetThemePreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<ThemePreference, Unit>() {
    override fun execute(param: ThemePreference) = repo.setTheme(param)
}
