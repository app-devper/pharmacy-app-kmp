@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AuthException
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.param.ChangePasswordParam
import app.devper.pharm.domain.param.CreateUserParam
import app.devper.pharm.domain.param.SetUserPasswordParam
import app.devper.pharm.domain.param.SetUserRoleParam
import app.devper.pharm.domain.param.SetUserStatusParam
import app.devper.pharm.domain.param.UpdateProfileParam
import app.devper.pharm.domain.param.UpdateUserParam
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.repository.FakeUsersRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun createParam(username: String = "newuser") = CreateUserParam(
    firstName = "Test", lastName = "User", username = username, password = "secret",
    phone = "0812345678", email = "test@example.com",
)

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

class CreateUserUseCaseTest {

    @Test
    fun forwards_param_and_returns_created_user() = runTest {
        val repo = FakeUsersRepository(initial = emptyList())
        val param = createParam(username = "alice")

        val user = CreateUserUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastCreate)
        assertEquals("alice", user.username)
        assertEquals(1, repo.snapshot.size)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeUsersRepository(createFailsWith = RuntimeException("duplicate username"))

        val result = CreateUserUseCase(repo, testDispatchers()).invoke(createParam())

        assertTrue(result.isFailure)
    }
}

class UpdateUserUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeUsersRepository()
        val param = UpdateUserParam(
            id = "u-1", firstName = "X", lastName = "Y", phone = "0", email = "x@y.z",
        )

        UpdateUserUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastUpdate)
    }
}

class DeleteUserUseCaseTest {

    @Test
    fun forwards_id_to_repository() = runTest {
        val repo = FakeUsersRepository()

        DeleteUserUseCase(repo, testDispatchers()).invoke("u-1").getOrThrow()

        assertEquals("u-1", repo.lastDelete)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeUsersRepository(deleteFailsWith = RuntimeException("forbidden"))

        val result = DeleteUserUseCase(repo, testDispatchers()).invoke("u-1")

        assertTrue(result.isFailure)
    }
}

class GetUsersUseCaseTest {

    @Test
    fun returns_repository_list() = runTest {
        val repo = FakeUsersRepository()

        val users = GetUsersUseCase(repo, testDispatchers()).invoke().getOrThrow()

        assertNotNull(users)
        assertEquals(repo.snapshot, users)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeUsersRepository(listFailsWith = RuntimeException("network"))

        val result = GetUsersUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

class SetUserPasswordUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeUsersRepository()
        val param = SetUserPasswordParam(id = "u-1", password = "newp")

        SetUserPasswordUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastSetPassword)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeUsersRepository(setPasswordFailsWith = RuntimeException("weak password"))

        val result = SetUserPasswordUseCase(repo, testDispatchers()).invoke(
            SetUserPasswordParam(id = "u-1", password = "x"),
        )

        assertTrue(result.isFailure)
        assertNull(repo.lastSetPassword)
    }
}

class SetUserRoleUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeUsersRepository()
        val param = SetUserRoleParam(id = "u-1", role = Role.ADMIN)

        SetUserRoleUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastSetRole)
    }
}

class SetUserStatusUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeUsersRepository()
        val param = SetUserStatusParam(id = "u-1", status = UmStatus.INACTIVE)

        SetUserStatusUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastSetStatus)
    }
}
