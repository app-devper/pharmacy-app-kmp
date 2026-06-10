@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.auth.LoginUseCase
import app.devper.pharm.domain.usecase.auth.LogoutUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.auth.LoginParam
import app.devper.pharm.domain.repository.FakeAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun testDispatchers() = UnconfinedTestDispatcher().let { d ->
    AppDispatchers(main = d, io = d, default = d)
}

class LoginUseCaseTest {

    @Test
    fun forwards_param_and_returns_user() = runTest {
        val repo = FakeAuthRepository()

        val user = LoginUseCase(repo, testDispatchers()).invoke(
            LoginParam(username = "alice", password = "pw", system = LoginUseCase.SYSTEM),
        ).getOrThrow()

        assertEquals("alice", user.username)
        assertEquals("alice", repo.lastLogin?.username)
        assertEquals(LoginUseCase.SYSTEM, repo.lastLogin?.system)
    }

    @Test
    fun convenience_invoke_trims_username_and_uses_pharmacy_system() = runTest {
        val repo = FakeAuthRepository()

        LoginUseCase(repo, testDispatchers()).invoke(
            username = "  alice  ", password = "pw",
        ).getOrThrow()

        assertEquals("alice", repo.lastLogin?.username)
        assertEquals("PHARMACY", repo.lastLogin?.system)
    }

    @Test
    fun login_flips_is_logged_in_state_to_true() = runTest {
        val repo = FakeAuthRepository()

        assertFalse(repo.isLoggedIn.first())
        LoginUseCase(repo, testDispatchers()).invoke(
            username = "alice", password = "pw",
        ).getOrThrow()

        assertTrue(repo.isLoggedIn.first())
    }

    @Test
    fun bad_credentials_wraps_in_result_failure() = runTest {
        val repo = FakeAuthRepository(loginThrowsOn = "alice")

        val result = LoginUseCase(repo, testDispatchers()).invoke(
            username = "alice", password = "wrong",
        )

        assertTrue(result.isFailure)
        assertNull(repo.lastLogin)
        assertFalse(repo.isLoggedIn.first())
    }
}

class LogoutUseCaseTest {

    @Test
    fun calls_repository_logout() = runTest {
        val repo = FakeAuthRepository()

        LogoutUseCase(repo, testDispatchers()).invoke().getOrThrow()

        assertTrue(repo.lastLogoutCalled)
    }

    @Test
    fun logout_flips_is_logged_in_state_to_false() = runTest {
        val repo = FakeAuthRepository()
        LoginUseCase(repo, testDispatchers()).invoke(
            username = "alice", password = "pw",
        ).getOrThrow()
        assertTrue(repo.isLoggedIn.first())

        LogoutUseCase(repo, testDispatchers()).invoke().getOrThrow()

        assertFalse(repo.isLoggedIn.first())
    }
}
