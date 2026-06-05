@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.presentation.profile

import app.devper.pharm.common.AuthException
import app.devper.pharm.domain.repository.FakeProfileRepository
import app.devper.pharm.domain.usecase.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.UpdateProfileUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
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
            setTheme = app.devper.pharm.domain.usecase.SetThemePreferenceUseCase(uiPrefs),
            setFontSize = app.devper.pharm.domain.usecase.SetFontSizePreferenceUseCase(uiPrefs),
            setDensity = app.devper.pharm.domain.usecase.SetDensityPreferenceUseCase(uiPrefs),
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
    }

    @Test
    fun update_sets_saving_then_saved_and_writes_param() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.onFirstName("สมหญิง")
        vm.onPhone("0999999999")
        assertTrue(vm.state.value.canSubmit)
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
        assertEquals("server boom", state.error)
    }

    @Test
    fun password_panel_opens_clears_and_resets() = runVmTest { dispatchers ->
        val fake = FakeProfileRepository()
        val vm = bundle(fake, dispatchers)
        advanceUntilIdle()
        vm.openPasswordPanel()
        vm.onOldPassword("old")
        vm.onNewPassword("new123")
        vm.onConfirmPassword("new123")
        assertTrue(vm.state.value.password.canSubmit)
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
        assertNotNull(state.passwordError)
        assertTrue(state.passwordError?.contains("รหัสผ่านเดิม") == true)
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
        assertEquals("token expired", state.error)
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
}
