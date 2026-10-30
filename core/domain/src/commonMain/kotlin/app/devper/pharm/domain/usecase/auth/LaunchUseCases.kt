package app.devper.pharm.domain.usecase.auth

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.LaunchTicket
import app.devper.pharm.common.platform.PasskeyAuthenticator
import app.devper.pharm.domain.model.User
import app.devper.pharm.domain.repository.auth.AuthRepository
import app.devper.pharm.domain.usecase.BaseUseCase
import kotlin.coroutines.cancellation.CancellationException

class ContinueFromUmUseCase(
    private val repository: AuthRepository,
    private val launch: LaunchTicket,
    dispatchers: AppDispatchers,
) : BaseUseCase<Unit, User?>(dispatchers) {
    val isPending: Boolean get() = launch.isPending

    override suspend fun execute(param: Unit): User? {
        val ticket = launch.peek() ?: return null
        return try {
            repository.exchangeTicket(ticket).also { launch.consume() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            launch.consume()
            throw e
        }
    }
}

/** Signs in with a passkey; the parameter is the username typed on the form, possibly empty. */
class LoginWithPasskeyUseCase(
    private val repository: AuthRepository,
    private val authenticator: PasskeyAuthenticator,
    dispatchers: AppDispatchers,
) : BaseUseCase<String, User>(dispatchers) {
    override suspend fun execute(param: String): User {
        val challenge = repository.passkeyLoginOptions()
        val credential = authenticator.get(challenge.optionsJson)
        return repository.loginWithPasskey(challenge.ceremonyId, credential, param.trim())
    }
}
