package app.devper.pharm.presentation.auth

import app.devper.pharm.presentation.auth.exception.LoginUiStateError

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.LaunchTicket
import app.devper.pharm.common.platform.PasskeyAuthenticator
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.repository.FakeAuthRepository
import app.devper.pharm.domain.repository.FakeLaunchTicket
import app.devper.pharm.domain.repository.FakePasskeyAuthenticator
import app.devper.pharm.domain.repository.FakeUiPreferencesRepository
import app.devper.pharm.domain.usecase.auth.ContinueFromUmUseCase
import app.devper.pharm.domain.usecase.auth.LoginUseCase
import app.devper.pharm.domain.usecase.auth.LoginWithPasskeyUseCase
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.observer.SessionExpiryProvider
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private fun newVm(
        dispatchers: AppDispatchers,
        repo: FakeAuthRepository = FakeAuthRepository(),
        uiPrefs: FakeUiPreferencesRepository = FakeUiPreferencesRepository(),
        sessionExpiry: SessionExpiryProvider = SessionExpiryProvider(),
        authenticator: PasskeyAuthenticator = PasskeyAuthenticator.Unsupported,
        launch: LaunchTicket = LaunchTicket.None,
    ): Triple<LoginViewModel, FakeAuthRepository, FakeUiPreferencesRepository> {
        val vm = LoginViewModel(
            login = LoginUseCase(repo, dispatchers),
            loginWithPasskey = LoginWithPasskeyUseCase(repo, authenticator, dispatchers),
            continueFromUm = ContinueFromUmUseCase(repo, launch, dispatchers),
            authenticator = authenticator,
            uiPreferences = UiPreferencesProvider(uiPrefs),
            preferences = uiPrefs,
            sessionExpiry = sessionExpiry,
        )
        return Triple(vm, repo, uiPrefs)
    }

    @Test
    fun the_passkey_button_shows_only_when_the_platform_supports_it() = runVmTest { dispatchers ->
        val (without) = newVm(dispatchers)
        val (with) = newVm(dispatchers, authenticator = FakePasskeyAuthenticator())
        advanceUntilIdle()
        assertFalse(without.state.value.passkeySupported)
        assertTrue(with.state.value.passkeySupported)
    }

    @Test
    fun submitPasskey_signs_in_without_the_form() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers, authenticator = FakePasskeyAuthenticator())
        vm.submitPasskey()
        advanceUntilIdle()
        assertEquals("passkey-user", vm.state.value.loggedInUser?.username)
        assertEquals(1, repo.passkeyLogins.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun a_refused_passkey_shows_a_passkey_error_and_keeps_the_form() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, repo = FakeAuthRepository(rejectPasskey = true), authenticator = FakePasskeyAuthenticator())
        vm.onUsernameChange("somsri")
        vm.submitPasskey()
        advanceUntilIdle()
        assertIs<LoginUiStateError.PasskeyFailed>(vm.state.value.errorState)
        assertEquals("somsri", vm.state.value.username)
        assertNull(vm.state.value.loggedInUser)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun opening_from_um_signs_in_without_the_form() = runVmTest { dispatchers ->
        val (vm, repo, _) = newVm(dispatchers, launch = FakeLaunchTicket("good-ticket"))
        advanceUntilIdle()
        assertEquals("from-um", vm.state.value.loggedInUser?.username)
        assertEquals(listOf("good-ticket"), repo.exchangedTickets)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun an_expired_link_from_um_shows_why_and_leaves_the_form_usable() = runVmTest { dispatchers ->
        val (vm, _, _) = newVm(dispatchers, launch = FakeLaunchTicket("old-ticket"))
        advanceUntilIdle()
        assertIs<LoginUiStateError.LaunchFailed>(vm.state.value.errorState)
        assertNull(vm.state.value.loggedInUser)
        assertFalse(vm.state.value.loading)
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
        assertEquals(true, vm.state.value.usernameMissing)
        assertEquals(false, vm.state.value.passwordMissing)
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
        assertEquals(false, vm.state.value.usernameMissing)
        assertEquals(true, vm.state.value.passwordMissing)
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
        assertFalse(vm.state.value.validationRequested)
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

    @Test
    fun expired_session_is_announced_on_the_login_screen() = runVmTest { dispatchers ->
        val sessionExpiry = SessionExpiryProvider()
        sessionExpiry.markExpired()
        val (vm) = newVm(dispatchers, sessionExpiry = sessionExpiry)
        advanceUntilIdle()
        assertTrue(vm.state.value.sessionExpired)
    }

    @Test
    fun signing_in_again_clears_the_expired_notice() = runVmTest { dispatchers ->
        val sessionExpiry = SessionExpiryProvider()
        sessionExpiry.markExpired()
        val (vm) = newVm(dispatchers, sessionExpiry = sessionExpiry)
        advanceUntilIdle()
        vm.onUsernameChange("somsri")
        vm.onPasswordChange("secret123")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.sessionExpired)
    }

    @Test
    fun a_fresh_launch_shows_no_expired_notice() = runVmTest { dispatchers ->
        val (vm) = newVm(dispatchers)
        advanceUntilIdle()
        assertFalse(vm.state.value.sessionExpired)
    }
}
