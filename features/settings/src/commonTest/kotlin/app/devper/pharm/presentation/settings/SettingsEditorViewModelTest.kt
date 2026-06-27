package app.devper.pharm.presentation.settings

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.settings.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.settings.UpdateSettingsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import app.devper.pharm.common.error.CommonUiStateMessage
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
}
