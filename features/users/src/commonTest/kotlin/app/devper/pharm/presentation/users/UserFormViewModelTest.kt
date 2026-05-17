@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.presentation.users

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.FakeUsersRepository
import app.devper.pharm.domain.usecase.CreateUserUseCase
import app.devper.pharm.domain.usecase.GetUsersUseCase
import app.devper.pharm.domain.usecase.UpdateUserUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserFormViewModelTest {

    private fun bundle(fake: FakeUsersRepository, dispatchers: AppDispatchers): UserFormViewModel =
        UserFormViewModel(
            getUsers = GetUsersUseCase(fake, dispatchers),
            createUser = CreateUserUseCase(fake, dispatchers),
            updateUser = UpdateUserUseCase(fake, dispatchers),
        )

    @Test
    fun add_canSubmit_requires_firstName_username_and_password() = runVmTest { dispatchers ->
        val fake = FakeUsersRepository()
        val vm = bundle(fake, dispatchers)
        vm.init(UserFormMode.Add)
        advanceUntilIdle()
        assertFalse(vm.state.value.canSubmit)
        vm.onFirstName("สมหมาย")
        assertFalse(vm.state.value.canSubmit)
        vm.onUsername("sommai")
        assertFalse(vm.state.value.canSubmit)
        vm.onPassword("short")
        assertFalse(vm.state.value.canSubmit)
        vm.onPassword("longenough")
        assertTrue(vm.state.value.canSubmit)
    }

    @Test
    fun add_happy_path_writes_create_param() = runVmTest { dispatchers ->
        val fake = FakeUsersRepository()
        val vm = bundle(fake, dispatchers)
        vm.init(UserFormMode.Add)
        advanceUntilIdle()
        vm.onFirstName(" สมหมาย ")
        vm.onLastName("ทดสอบ")
        vm.onUsername("sommai")
        vm.onPassword("password1")
        vm.onPhone("0811111111")
        vm.onEmail("sommai@example.com")
        vm.submit()
        advanceUntilIdle()
        val p = fake.lastCreate
        assertNotNull(p)
        assertEquals("สมหมาย", p.firstName)
        assertEquals("sommai", p.username)
        assertEquals("password1", p.password)
        assertEquals("PHA", p.clientId)
        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.saving)
    }

    @Test
    fun edit_init_hydrates_form_from_existing_user() = runVmTest { dispatchers ->
        val fake = FakeUsersRepository()
        val vm = bundle(fake, dispatchers)
        vm.init(UserFormMode.Edit("u-1"))
        advanceUntilIdle()
        val state = vm.state.value
        assertEquals("สมชาย", state.form.firstName)
        assertEquals("ใจดี", state.form.lastName)
        assertEquals("somchai", state.form.username)
        assertFalse(state.loading)
    }

    @Test
    fun edit_happy_path_writes_update_param() = runVmTest { dispatchers ->
        val fake = FakeUsersRepository()
        val vm = bundle(fake, dispatchers)
        vm.init(UserFormMode.Edit("u-1"))
        advanceUntilIdle()
        vm.onFirstName("สมหญิงใหม่")
        vm.onPhone("0999999999")
        vm.submit()
        advanceUntilIdle()
        val p = fake.lastUpdate
        assertNotNull(p)
        assertEquals("u-1", p.id)
        assertEquals("สมหญิงใหม่", p.firstName)
        assertEquals("0999999999", p.phone)
        assertNull(fake.lastCreate)
    }

    @Test
    fun save_failure_surfaces_error() = runVmTest { dispatchers ->
        val fake = FakeUsersRepository(createFailsWith = RuntimeException("server boom"))
        val vm = bundle(fake, dispatchers)
        vm.init(UserFormMode.Add)
        advanceUntilIdle()
        vm.onFirstName("สมหมาย")
        vm.onUsername("sommai")
        vm.onPassword("password1")
        vm.submit()
        advanceUntilIdle()
        val state = vm.state.value
        assertEquals("server boom", state.error)
        assertFalse(state.saved)
        assertFalse(state.saving)
    }
}
