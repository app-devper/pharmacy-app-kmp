package app.devper.pharm.domain.usecase.settings

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository

class SetFontSizePreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<FontSizePreference, Unit>() {
    override fun execute(param: FontSizePreference) = repo.setFontSize(param)
}
