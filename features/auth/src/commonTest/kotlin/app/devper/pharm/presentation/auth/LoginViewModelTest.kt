package app.devper.pharm.presentation.auth

import app.devper.pharm.presentation.auth.exception.LoginUiStateError

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.repository.FakeAuthRepository
import app.devper.pharm.domain.repository.FakeUiPreferencesRepository
import app.devper.pharm.domain.usecase.auth.LoginUseCase
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.usecase.settings.SetLastUsernameUseCase
import app.devper.pharm.domain.usecase.settings.SetLocalePreferenceUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
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
            setLastUsername = SetLastUsernameUseCase(uiPrefs),
        )
        return Triple(vm, repo, uiPrefs)
    }

    @Test
    fun last_username_prefills_when_field_is_blank() = runVmTest { dispatchers ->
        val prefs = FakeUiPreferencesRepository(initial = UiPreferences(lastUsername = "somsri"))
        val (vm) = newVm(dispatchers, uiPrefs = prefs)
        advanceUntilIdle()
        assertEquals("somsri", vm.state.value.username)
    }

    @Test
    fun successful_login_remembers_the_username() = runVmTest { dispatchers ->
        val (vm, _, prefs) = newVm(dispatchers)
        vm.onUsernameChange("somsri")
        vm.onPasswordChange("secret123")
        vm.submit()
        advanceUntilIdle()
        assertEquals("somsri", prefs.state.value.lastUsername)
    }

    @Test
    fun submit_with_blank_username_short_circuits() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers)
        vm.onPasswordChange("secret")
        vm.submit()
        advanceUntilIdle()
        assertIs<LoginUiStateError.RequiredFields>(vm.state.value.errorState)
        assertNull(repo.lastLogin)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun submit_with_blank_password_short_circuits() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers)
        vm.onUsernameChange("admin")
        vm.submit()
        advanceUntilIdle()
        assertIs<LoginUiStateError.RequiredFields>(vm.state.value.errorState)
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
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun submit_failure_routes_to_error_not_loggedIn() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, FakeAuthRepository(loginThrowsOn = "baduser"))
        vm.onUsernameChange("baduser")
        vm.onPasswordChange("badpwd")
        vm.submit()
        advanceUntilIdle()
        val s = vm.state.value
        assertIs<LoginUiStateError.LoginFailed>(s.errorState)
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
        assertNotNull(vm.state.value.errorState)
        vm.onUsernameChange("a")
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun initial_locale_mirrors_persisted_preference() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        advanceUntilIdle()
        assertEquals("th", vm.state.value.locale)
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
        vm.onLocaleChange("th")
        advanceUntilIdle()
        assertNull(uiPrefs.lastLocale)
    }

    @Test
    fun onPasswordChange_clears_error_side_effect() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers)
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.errorState)
        vm.onPasswordChange("x")
        assertNull(vm.state.value.errorState)
    }
}
