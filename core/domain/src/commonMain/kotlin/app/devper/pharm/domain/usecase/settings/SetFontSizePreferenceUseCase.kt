package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.repository.UiPreferencesRepository

class SetFontSizePreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<FontSizePreference, Unit>() {
    override fun execute(param: FontSizePreference) = repo.setFontSize(param)
}
