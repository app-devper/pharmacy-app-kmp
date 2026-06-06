package app.devper.pharm.presentation.settings

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.UpdateSettingsUseCase
import app.devper.pharm.ui.common.runVmTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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
        assertEquals("บันทึกแล้ว", model.state.value.message)
        assertFalse(model.state.value.saving)
    }
}
