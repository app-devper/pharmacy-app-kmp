package app.devper.pharm.domain.repository.auth

import app.devper.pharm.domain.model.PasskeyChallenge
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.param.auth.LoginParam
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>

    suspend fun login(param: LoginParam): User

    suspend fun exchangeTicket(ticket: String): User

    suspend fun passkeyLoginOptions(): PasskeyChallenge

    /** Finishes a passkey login; a non-blank [username] is the one typed, and UM refuses another account's passkey. */
    suspend fun loginWithPasskey(ceremonyId: String, credentialJson: String, username: String): User

    suspend fun logout()
}
