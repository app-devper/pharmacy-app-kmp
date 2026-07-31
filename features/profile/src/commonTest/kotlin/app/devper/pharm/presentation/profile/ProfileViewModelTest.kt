@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.presentation.profile

import app.devper.pharm.presentation.profile.exception.ProfileUiStateError

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.usecase.settings.SetDensityPreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetFontSizePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetLocalePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetThemePreferenceUseCase
import app.devper.pharm.domain.usecase.profile.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.profile.GetProfileUseCase
import app.devper.pharm.domain.usecase.profile.UpdateProfileUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfileViewModelTest {

    private fun bundle(fake: FakeProfileRepository, dispatchers: app.devper.pharm.common.AppDispatchers): ProfileViewModel {
        val uiPrefs = app.devper.pharm.domain.repository.FakeUiPreferencesRepository()
        return ProfileViewModel(
            getProfile = GetProfileUseCase(fake, dispatchers),
            updateProfile = UpdateProfileUseCase(fake, dispatchers),
            changePassword = ChangePasswordUseCase(fake, dispatchers),
            uiPreferences = app.devper.pharm.domain.observer.UiPreferencesProvider(uiPrefs),
            setTheme = SetThemePreferenceUseCase(uiPrefs),
            setFontSize = SetFontSizePreferenceUseCase(uiPrefs),
            setDensity = SetDensityPreferenceUseCase(uiPrefs),
            setLocale = SetLocalePreferenceUseCase(uiPrefs),
        )
    }

    @Test
    fun loads_profile_on_init() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.loading)
        assertNotNull(state.user)
        assertEquals("สมชาย", state.form.firstName)
        assertEquals("0812345678", state.form.phone)
        assertFalse(state.hasUnsavedChanges)
    }

    @Test
    fun update_sets_saving_then_saved_and_writes_param() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.onFirstName("สมหญิง")
        vm.onPhone("0999999999")
        assertTrue(vm.state.value.canSubmit)
        assertTrue(vm.state.value.hasUnsavedChanges)
        vm.submit()
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.saving)
        assertTrue(state.saved)
        assertEquals("สมหญิง", state.user?.firstName)
        assertEquals("0999999999", fake.lastUpdate?.phone)
    }

    @Test
    fun update_failure_surfaces_error() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository(updateFailsWith = RuntimeException("server boom"))
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.onFirstName("Edited")
        vm.submit()
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.saving)
        assertFalse(state.saved)
        assertIs<CommonUiStateError.SaveFailed>(state.errorState)
    }

    @Test
    fun password_panel_opens_clears_and_resets() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("old")
        vm.onNewPassword("new12345")
        vm.onConfirmPassword("new12345")
        assertTrue(vm.state.value.password.canSubmit)
    }

    @Test
    fun password_shorter_than_eight_characters_blocks_submit() = runVmTest { dispatchers ->
        val vm = bundle(FakeProfileRepository(), dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("old-password")
        vm.onNewPassword("short")
        vm.onConfirmPassword("short")
        assertFalse(vm.state.value.password.newPasswordValid)
        assertFalse(vm.state.value.password.canSubmit)
    }

    @Test
    fun blank_required_name_can_be_attempted_but_not_submitted() = runVmTest { dispatchers ->
        val vm = bundle(FakeProfileRepository(), dispatchers)
        advanceUntilIdle()
        vm.onFirstName("")
        assertTrue(vm.state.value.canAttemptSubmit)
        assertFalse(vm.state.value.canSubmit)
    }

    @Test
    fun change_password_success_closes_panel_and_clears_form() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository(expectedOldPassword = "correct")
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("correct")
        vm.onNewPassword("brand-new")
        vm.onConfirmPassword("brand-new")
        vm.submitPasswordChange()
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.passwordSaving)
        assertTrue(state.passwordSaved)
        assertFalse(state.showPasswordPanel)
        assertEquals("", state.password.oldPassword)
        assertEquals("correct", fake.lastChangePassword?.oldPassword)
    }

    @Test
    fun change_password_wrong_old_surfaces_error() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository(expectedOldPassword = "correct")
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("wrong")
        vm.onNewPassword("brand-new")
        vm.onConfirmPassword("brand-new")
        vm.submitPasswordChange()
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.passwordSaved)
        assertIs<ProfileUiStateError.PasswordChangeFailed>(state.passwordErrorState)
        assertTrue(state.passwordErrorState?.cause?.message?.contains("รหัสผ่านเดิม") == true)
    }

    @Test
    fun confirm_mismatch_blocks_submit() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("old")
        vm.onNewPassword("aaa")
        vm.onConfirmPassword("bbb")
        assertFalse(vm.state.value.password.canSubmit)
        assertNull(fake.lastChangePassword)
    }

    @Test
    fun load_failure_surfaces_error() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository(getFailsWith = AuthException("token expired"))
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        val state = vm.state.value
        assertFalse(state.loading)
        assertNull(state.user)
        val loadErr = state.errorState
        assertIs<ProfileUiStateError.LoadProfileFailed>(loadErr)
        assertEquals("token expired", loadErr.cause?.message)
    }

    @Test
    fun density_change_is_reflected_in_state() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.onDensityChange("compact")
        advanceUntilIdle()
        assertEquals("compact", vm.state.value.density)
    }

    @Test
    fun locale_change_is_reflected_in_state_and_surfaces_restart_message() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        assertEquals("th", vm.state.value.locale)
        assertFalse(vm.state.value.localeChangeApplied)

        vm.onLocaleChange("en")
        advanceUntilIdle()

        assertEquals("en", vm.state.value.locale)
        assertTrue(vm.state.value.localeChangeApplied)
    }

    @Test
    fun locale_change_to_same_value_does_not_surface_restart_message() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.onLocaleChange("th")
        advanceUntilIdle()
        assertFalse(vm.state.value.localeChangeApplied)
    }

    @Test
    fun dismiss_locale_change_message_clears_it() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.onLocaleChange("en")
        advanceUntilIdle()
        assertTrue(vm.state.value.localeChangeApplied)
        vm.dismissLocaleChangeMessage()
        advanceUntilIdle()
        assertFalse(vm.state.value.localeChangeApplied)
    }

    @Test
    fun close_password_panel_hides_panel() = runVmTest { dispatchers ->
        val vm = bundle(FakeProfileRepository(), dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        assertTrue(vm.state.value.showPasswordPanel)
        vm.closePasswordPanel()
        assertFalse(vm.state.value.showPasswordPanel)
    }

    @Test
    fun dismiss_password_error_clears_it() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository(changePasswordFailsWith = RuntimeException("change failed"))
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("old")
        vm.onNewPassword("new12345")
        vm.onConfirmPassword("new12345")
        vm.submitPasswordChange()
        advanceUntilIdle()
        assertNotNull(vm.state.value.passwordErrorState)
        vm.dismissPasswordError()
        assertNull(vm.state.value.passwordErrorState)
    }

    @Test
    fun reload_refreshes_profile_and_clears_loading() = runVmTest { dispatchers ->
        val vm = bundle(FakeProfileRepository(), dispatchers)
        advanceUntilIdle()
        assertNotNull(vm.state.value.user)
        vm.reload()
        advanceUntilIdle()
        assertNotNull(vm.state.value.user)
        assertFalse(vm.state.value.loading)
    }
}
