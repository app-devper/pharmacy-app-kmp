package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.repository.UiPreferencesRepository

class SetLocalePreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<LocalePreference, Unit>() {
    override fun execute(param: LocalePreference) = repo.setLocale(param)
}
