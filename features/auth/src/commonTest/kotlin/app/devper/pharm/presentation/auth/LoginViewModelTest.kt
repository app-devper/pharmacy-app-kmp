package app.devper.pharm.presentation.auth

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.repository.FakeAuthRepository
import app.devper.pharm.domain.repository.FakeUiPreferencesRepository
import app.devper.pharm.domain.usecase.LoginUseCase
import app.devper.pharm.domain.usecase.SetLocalePreferenceUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeAuthRepository = FakeAuthRepository(),
        uiPrefs: FakeUiPreferencesRepository = FakeUiPreferencesRepository(),
    ): Triple<LoginViewModel, FakeAuthRepository, FakeUiPreferencesRepository> {
        val vm = LoginViewModel(
            login = LoginUseCase(repo, dispatchers),
            uiPreferences = UiPreferencesProvider(uiPrefs),
            setLocale = SetLocalePreferenceUseCase(uiPrefs),
        )
        return Triple(vm, repo, uiPrefs)
    }

    @Test
    fun submit_with_blank_username_short_circuits() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers)
        vm.onPasswordChange("secret")
        vm.submit()
        advanceUntilIdle()
        assertEquals("กรุณากรอกชื่อผู้ใช้และรหัสผ่าน", vm.state.value.error)
        assertNull(repo.lastLogin)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun submit_with_blank_password_short_circuits() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers)
        vm.onUsernameChange("admin")
        vm.submit()
        advanceUntilIdle()
        assertEquals("กรุณากรอกชื่อผู้ใช้และรหัสผ่าน", vm.state.value.error)
        assertNull(repo.lastLogin)
    }

    @Test
    fun submit_happy_path_calls_login_with_trimmed_username_and_pharmacy_system() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers)
        vm.onUsernameChange("  admin  ")
        vm.onPasswordChange("secret")
        vm.submit()
        advanceUntilIdle()
        val captured = repo.lastLogin
        assertNotNull(captured)
        assertEquals("admin", captured.username)
        assertEquals("secret", captured.password)
        assertEquals("PHARMACY", captured.system)
        assertNotNull(vm.state.value.loggedInUser)
        assertEquals("admin", vm.state.value.loggedInUser?.username)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun submit_failure_routes_to_error_not_loggedIn() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, FakeAuthRepository(loginThrowsOn = "baduser"))
        vm.onUsernameChange("baduser")
        vm.onPasswordChange("badpwd")
        vm.submit()
        advanceUntilIdle()
        val s = vm.state.value
        assertNotNull(s.error)
        assertNull(s.loggedInUser)
        assertFalse(s.loading)
    }

    @Test
    fun submit_failure_clears_password_to_prevent_memory_leak() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, FakeAuthRepository(loginThrowsOn = "baduser"))
        vm.onUsernameChange("baduser")
        vm.onPasswordChange("supersecret123")
        vm.submit()
        advanceUntilIdle()
        assertEquals("", vm.state.value.password)
        assertEquals("baduser", vm.state.value.username)
    }

    @Test
    fun onUsernameChange_clears_error_side_effect() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.onUsernameChange("a")
        assertNull(vm.state.value.error)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }

    @Test
    fun initial_locale_mirrors_persisted_preference() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals("system", vm.state.value.locale)
    }

    @Test
    fun onLocaleChange_persists_and_updates_state() = runVmTest { dispatchers ->
        val (vm, _, uiPrefs) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onLocaleChange("en")
        advanceUntilIdle()
        assertEquals("en", vm.state.value.locale)
        assertEquals(app.devper.pharm.domain.model.LocalePreference.En, uiPrefs.lastLocale)
    }

    @Test
    fun onLocaleChange_to_same_value_does_not_call_persist() = runVmTest { dispatchers ->
        val (vm, _, uiPrefs) = newVm(dispatchers)
        advanceUntilIdle()
        vm.onLocaleChange("system")
        advanceUntilIdle()
        assertNull(uiPrefs.lastLocale)
    }
}
