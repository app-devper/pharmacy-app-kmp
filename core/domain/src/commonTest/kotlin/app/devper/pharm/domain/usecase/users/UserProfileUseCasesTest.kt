@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.profile.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.profile.GetProfileUseCase
import app.devper.pharm.domain.usecase.profile.UpdateProfileUseCase

import app.devper.pharm.common.AuthException
import app.devper.pharm.domain.param.profile.ChangePasswordParam
import app.devper.pharm.domain.param.profile.UpdateProfileParam
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetProfileUseCaseTest {

    @Test
    fun returns_repository_profile() = runTest {
        val repo = FakeProfileRepository()

        val result = GetProfileUseCase(repo, testDispatchers()).invoke()

        assertEquals(repo.snapshot, result.getOrThrow())
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeProfileRepository(getFailsWith = RuntimeException("unauthorized"))

        val result = GetProfileUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

class UpdateProfileUseCaseTest {

    @Test
    fun forwards_param_and_returns_updated_profile() = runTest {
        val repo = FakeProfileRepository()
        val param = UpdateProfileParam(
            firstName = "Updated", lastName = "Name", phone = "0900000000", email = "u@a.com",
        )

        val user = UpdateProfileUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastUpdate)
        assertEquals("Updated", user.firstName)
        assertEquals("u@a.com", user.email)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeProfileRepository(updateFailsWith = RuntimeException("denied"))
        val param = UpdateProfileParam("a", "b", "0", "x@y.z")

        val result = UpdateProfileUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class ChangePasswordUseCaseTest {

    @Test
    fun forwards_param_to_repository_when_old_matches() = runTest {
        val repo = FakeProfileRepository(expectedOldPassword = "oldp")
        val param = ChangePasswordParam(oldPassword = "oldp", newPassword = "newp")

        ChangePasswordUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastChangePassword)
    }

    @Test
    fun wrong_old_password_fails_with_auth_exception() = runTest {
        val repo = FakeProfileRepository(expectedOldPassword = "correct")
        val param = ChangePasswordParam(oldPassword = "wrong", newPassword = "any")

        val result = ChangePasswordUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthException)
    }
}
