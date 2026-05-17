package app.devper.pharm.domain.observer

import app.devper.pharm.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthStateProvider(private val auth: AuthRepository) {
    val isLoggedIn: Flow<Boolean> get() = auth.isLoggedIn
}
