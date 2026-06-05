package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.repository.UiPreferencesRepository

class SetDensityPreferenceUseCase(private val repo: UiPreferencesRepository) : BaseSyncUseCase<DensityPreference, Unit>() {
    override fun execute(param: DensityPreference) = repo.setDensity(param)
}
