@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.presentation.users

import app.devper.pharm.common.error.ErrorMessages

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.repository.FakeUsersRepository
import app.devper.pharm.domain.usecase.DeleteUserUseCase
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.GetUsersUseCase
import app.devper.pharm.domain.usecase.SetUserPasswordUseCase
import app.devper.pharm.domain.usecase.SetUserRoleUseCase
import app.devper.pharm.domain.usecase.SetUserStatusUseCase
import app.devper.pharm.domain.extension.canManage
import app.devper.pharm.domain.extension.canManageUsers
import app.devper.pharm.domain.extension.canViewUsers
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UsersListViewModelTest {

    private fun bundle(
        users: FakeUsersRepository,
        profile: FakeProfileRepository,
        dispatchers: AppDispatchers,
    ): UsersListViewModel = UsersListViewModel(
        getProfile = GetProfileUseCase(profile, dispatchers),
        getUsers = GetUsersUseCase(users, dispatchers),
        deleteUser = DeleteUserUseCase(users, dispatchers),
        setUserRole = SetUserRoleUseCase(users, dispatchers),
        setUserStatus = SetUserStatusUseCase(users, dispatchers),
        setUserPassword = SetUserPasswordUseCase(users, dispatchers),
    )

    @Test
    fun load_happy_populates_users_and_current_role() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.loading)
        assertEquals(3, state.users.size)
        assertEquals(Role.ADMIN, state.currentUserRole)
        assertEquals(FakeProfileRepository.sampleUser.id, state.currentUserId)
    }

    @Test
    fun search_filters_users_by_name_or_username() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        vm.setSearch("somchai")
        val filtered = vm.state.value.filtered
        assertEquals(1, filtered.size)
        assertEquals("somchai", filtered.first().username)
    }

    @Test
    fun delete_confirm_calls_repo_and_clears_action() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somying" }
        vm.requestDelete(target)
        assertEquals(target, vm.state.value.actionTarget)
        vm.confirmDelete()
        advanceUntilIdle()
        val state = vm.state.value
        assertNull(state.actionTarget)
        assertNull(state.actionMode)
        assertEquals(target.id, users.lastDelete)
        assertFalse(state.users.any { it.id == target.id })
    }

    @Test
    fun submit_role_change_writes_role_enum_and_reloads() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somying" }
        vm.requestRoleEdit(target)
        vm.submitRoleChange(Role.ADMIN)
        advanceUntilIdle()
        assertEquals(target.id, users.lastSetRole?.id)
        assertEquals(Role.ADMIN, users.lastSetRole?.role)
        assertNull(vm.state.value.actionTarget)
        assertEquals(Role.ADMIN, vm.state.value.users.first { it.id == target.id }.role)
    }

    @Test
    fun status_toggle_flips_active() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somchai" }
        vm.requestStatusToggle(target)
        vm.confirmStatusToggle()
        advanceUntilIdle()
        assertEquals(UmStatus.INACTIVE, users.lastSetStatus?.status)
    }

    @Test
    fun password_set_passes_string_through() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somchai" }
        vm.requestPasswordSet(target)
        vm.submitPasswordSet("brand-new-pwd")
        advanceUntilIdle()
        assertEquals(target.id, users.lastSetPassword?.id)
        assertEquals("brand-new-pwd", users.lastSetPassword?.password)
    }

    @Test
    fun cannot_manage_self_per_validator() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val state = vm.state.value
        val me = state.users.firstOrNull { it.id == state.currentUserId }
        assertNotNull(me)
        assertFalse(
            state.currentUserRole.canManage(
                target = me.role,
                isSelf = me.id == state.currentUserId,
            ),
        )
    }

    @Test
    fun load_failure_surfaces_error() = runVmTest { dispatchers ->
        val users = FakeUsersRepository(listFailsWith = RuntimeException("network down"))
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.loading)
        assertEquals(0, state.users.size)
        assertEquals(ErrorMessages.LOAD_FAILED, state.error)
    }

    @Test
    fun delete_failure_clears_action_and_sets_error() = runVmTest { dispatchers ->
        val users = FakeUsersRepository(deleteFailsWith = RuntimeException("403"))
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somying" }
        vm.requestDelete(target)
        vm.confirmDelete()
        advanceUntilIdle()
        val state = vm.state.value
        assertNull(state.actionTarget)
        assertEquals("403", state.error)
    }

    @Test
    fun dismiss_action_clears_target_and_mode() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somying" }
        vm.requestRoleEdit(target)
        assertNotNull(vm.state.value.actionTarget)
        vm.dismissAction()
        assertNull(vm.state.value.actionTarget)
        assertNull(vm.state.value.actionMode)
    }

    @Test
    fun unknown_role_change_is_no_op() = runVmTest { dispatchers ->
        val users = FakeUsersRepository()
        val profile = FakeProfileRepository()
        val vm = bundle(users, profile, dispatchers)
        advanceUntilIdle()
        val target = users.snapshot.first { it.username == "somying" }
        vm.requestRoleEdit(target)
        vm.submitRoleChange(Role.UNKNOWN)
        advanceUntilIdle()
        assertNull(users.lastSetRole)
        assertTrue(vm.state.value.actionTarget != null)
    }
}
