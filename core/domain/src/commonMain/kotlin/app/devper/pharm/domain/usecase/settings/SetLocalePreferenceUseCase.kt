package app.devper.pharm.domain.usecase.settings

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository

class SetLocalePreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<LocalePreference, Unit>() {
    override fun execute(param: LocalePreference) = repo.setLocale(param)
}
