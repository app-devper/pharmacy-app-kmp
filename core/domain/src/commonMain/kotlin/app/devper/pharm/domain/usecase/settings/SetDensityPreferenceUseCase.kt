package app.devper.pharm.domain.usecase.settings

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository

class SetDensityPreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<DensityPreference, Unit>() {
    override fun execute(param: DensityPreference) = repo.setDensity(param)
}
