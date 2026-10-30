@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.LaunchExpiredException
import app.devper.pharm.common.PasskeyCancelledException
import app.devper.pharm.common.platform.LaunchTicket
import app.devper.pharm.domain.repository.FakeAuthRepository
import app.devper.pharm.domain.repository.FakeLaunchTicket
import app.devper.pharm.domain.repository.FakePasskeyAuthenticator
import app.devper.pharm.domain.usecase.auth.ContinueFromUmUseCase
import app.devper.pharm.domain.usecase.auth.LoginWithPasskeyUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun testDispatchers() = UnconfinedTestDispatcher().let { d ->
    AppDispatchers(main = d, io = d, default = d)
}

class ContinueFromUmUseCaseTest {

    @Test
    fun a_ticket_from_um_signs_in() = runTest {
        val repo = FakeAuthRepository()

        val user = ContinueFromUmUseCase(repo, FakeLaunchTicket("good-ticket"), testDispatchers()).invoke(Unit).getOrThrow()

        assertEquals("from-um", user?.username)
        assertEquals(listOf("good-ticket"), repo.exchangedTickets)
        assertTrue(repo.isLoggedIn.first())
    }

    @Test
    fun no_ticket_does_nothing() = runTest {
        val repo = FakeAuthRepository()

        val user = ContinueFromUmUseCase(repo, LaunchTicket.None, testDispatchers()).invoke(Unit).getOrThrow()

        assertNull(user)
        assertTrue(repo.exchangedTickets.isEmpty())
    }

    @Test
    fun a_cancelled_exchange_keeps_the_ticket_for_the_next_attempt() = runTest {
        val repo = FakeAuthRepository(hangExchange = true)
        val ticket = FakeLaunchTicket("good-ticket")
        val job = launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
            ContinueFromUmUseCase(repo, ticket, testDispatchers()).invoke(Unit)
        }

        job.cancel()
        job.join()

        assertTrue(ticket.isPending)
        assertEquals(listOf("good-ticket"), repo.exchangedTickets)
    }

    @Test
    fun a_refused_ticket_fails_once_and_is_not_retried() = runTest {
        val repo = FakeAuthRepository()
        val useCase = ContinueFromUmUseCase(repo, FakeLaunchTicket("old"), testDispatchers())

        assertIs<LaunchExpiredException>(useCase(Unit).exceptionOrNull())
        assertNull(useCase(Unit).getOrThrow())
        assertEquals(listOf("old"), repo.exchangedTickets)
        assertFalse(repo.isLoggedIn.first())
    }
}

class LoginWithPasskeyUseCaseTest {

    @Test
    fun answers_the_ceremony_um_issued_and_signs_in() = runTest {
        val repo = FakeAuthRepository()
        val authenticator = FakePasskeyAuthenticator()

        val user = LoginWithPasskeyUseCase(repo, authenticator, testDispatchers()).invoke(" somsri ").getOrThrow()

        assertEquals("passkey-user", user.username)
        assertEquals(listOf("{\"publicKey\":{}}"), authenticator.seenOptions)
        assertEquals(listOf(Triple("c1", "{\"id\":\"cred\"}", "somsri")), repo.passkeyLogins)
    }

    @Test
    fun a_dismissed_prompt_signs_nobody_in() = runTest {
        val repo = FakeAuthRepository()

        val result = LoginWithPasskeyUseCase(repo, FakePasskeyAuthenticator(cancel = true), testDispatchers()).invoke("")

        assertIs<PasskeyCancelledException>(result.exceptionOrNull())
        assertTrue(repo.passkeyLogins.isEmpty())
        assertFalse(repo.isLoggedIn.first())
    }
}
