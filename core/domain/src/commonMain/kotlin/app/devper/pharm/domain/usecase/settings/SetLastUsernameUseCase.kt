package app.devper.pharm.domain.usecase.settings

import app.devper.pharm.domain.repository.settings.UiPreferencesRepository

class SetLastUsernameUseCase(private val repo: UiPreferencesRepository) {
    operator fun invoke(username: String) = repo.setLastUsername(username)
}
