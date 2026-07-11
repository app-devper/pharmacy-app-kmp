package app.devper.pharm.presentation.settings

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.settings.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.settings.UpdateSettingsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import app.devper.pharm.common.error.CommonUiStateMessage
import app.devper.pharm.presentation.settings.exception.SettingsUiStateError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsEditorViewModelTest {

    private fun vm(repo: FakeSettingsRepository, d: app.devper.pharm.common.AppDispatchers) =
        SettingsEditorViewModel(
            SettingsProvider(repo),
            RefreshSettingsUseCase(repo, d),
            UpdateSettingsUseCase(repo, d),
        )

    @Test
    fun enabling_ky_skip_requires_confirmation() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onKySkipAuto(true)
        assertTrue(model.state.value.confirmKySkip)
        assertFalse(model.state.value.form.kySkipAuto)
    }

    @Test
    fun cancelling_ky_skip_confirmation_keeps_it_off() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onKySkipAuto(true)
        model.cancelKySkipAuto()
        assertFalse(model.state.value.confirmKySkip)
        assertFalse(model.state.value.form.kySkipAuto)
    }

    @Test
    fun confirming_ky_skip_enables_it() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onKySkipAuto(true)
        model.confirmKySkipAuto()
        assertFalse(model.state.value.confirmKySkip)
        assertTrue(model.state.value.form.kySkipAuto)
    }

    @Test
    fun disabling_ky_skip_needs_no_confirmation() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onKySkipAuto(true)
        model.confirmKySkipAuto()
        model.onKySkipAuto(false)
        assertFalse(model.state.value.confirmKySkip)
        assertFalse(model.state.value.form.kySkipAuto)
    }

    @Test
    fun init_finishes_loading_and_refreshes() = runVmTest { d ->
        val repo = FakeSettingsRepository()
        val model = vm(repo, d)
        advanceUntilIdle()
        assertFalse(model.state.value.loading)
        assertEquals(1, repo.refreshCallCount)
    }

    @Test
    fun select_tab_updates_tab() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.selectTab(SettingsTab.Stock)
        assertEquals(SettingsTab.Stock, model.state.value.tab)
    }

    @Test
    fun editing_store_name_marks_state_dirty_and_savable() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onStoreName("ร้านยาทดสอบ")
        assertEquals("ร้านยาทดสอบ", model.state.value.form.storeName)
        assertTrue(model.state.value.dirty)
        assertTrue(model.state.value.canSave)
    }

    @Test
    fun submit_calls_update_and_sets_message() = runVmTest { d ->
        val repo = FakeSettingsRepository()
        val model = vm(repo, d)
        advanceUntilIdle()
        model.onStoreName("ร้านยาทดสอบ")
        model.submit()
        advanceUntilIdle()
        assertNotNull(repo.lastUpdate)
        assertIs<CommonUiStateMessage.Saved>(model.state.value.messageState)
        assertFalse(model.state.value.saving)
    }

    @Test
    fun dismiss_message_clears_message_state() = runVmTest { d ->
        val repo = FakeSettingsRepository()
        val model = vm(repo, d)
        advanceUntilIdle()
        model.onStoreName("ร้านยาทดสอบ")
        model.submit()
        advanceUntilIdle()
        assertNotNull(model.state.value.messageState)
        model.dismissMessage()
        assertNull(model.state.value.messageState)
    }

    @Test
    fun dirty_resets_to_false_after_successful_save() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onStoreName("ร้านยาทดสอบ")
        assertTrue(model.state.value.dirty)
        model.submit()
        advanceUntilIdle()
        assertFalse(model.state.value.dirty)
    }

    @Test
    fun submit_failure_sets_error_and_clears_saving() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(updateThrows = true), d)
        advanceUntilIdle()
        model.onStoreName("ร้านยาใหม่")
        model.submit()
        advanceUntilIdle()
        assertNotNull(model.state.value.errorState)
        assertFalse(model.state.value.saving)
        assertNull(model.state.value.messageState)
    }

    @Test
    fun refresh_failure_sets_load_error() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(refreshThrows = true), d)
        advanceUntilIdle()
        assertIs<SettingsUiStateError.LoadSettingsFailed>(model.state.value.errorState)
        assertFalse(model.state.value.loading)
    }

    @Test
    fun editing_multiple_fields_marks_all_dirty() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d)
        advanceUntilIdle()
        model.onStoreAddress("123 ถ.สุขุมวิท")
        model.onStorePhone("02-123-4567")
        model.onPharmacistName("ภ.ก. สมชาย")
        assertTrue(model.state.value.dirty)
        assertEquals("123 ถ.สุขุมวิท", model.state.value.form.storeAddress)
        assertEquals("02-123-4567", model.state.value.form.storePhone)
        assertEquals("ภ.ก. สมชาย", model.state.value.form.pharmacistName)
    }
}
