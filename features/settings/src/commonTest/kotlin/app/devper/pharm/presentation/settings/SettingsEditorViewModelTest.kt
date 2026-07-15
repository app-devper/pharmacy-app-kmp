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

    @Test
    fun form_validation_routes_to_the_first_invalid_tab() {
        val valid = SettingsFormFields(storeName = "Store")
        assertTrue(valid.valid)

        assertEquals(SettingsTab.Store, valid.copy(timezone = "Bangkok").firstInvalidTab)
        assertEquals(SettingsTab.Receipt, valid.copy(receiptPaperWidth = "59").firstInvalidTab)
        assertEquals(SettingsTab.Stock, valid.copy(stockReorderDays = "0").firstInvalidTab)
        assertEquals(SettingsTab.Stock, valid.copy(stockReorderLookahead = "181").firstInvalidTab)
        assertEquals(SettingsTab.Stock, valid.copy(stockExpiringDays = "").firstInvalidTab)
    }

    private class StubPrinter(private val result: Boolean = true) : app.devper.pharm.common.print.ReceiptPrinter {
        var printed: app.devper.pharm.common.print.ReceiptTemplate? = null
        override fun print(template: app.devper.pharm.common.print.ReceiptTemplate): Boolean {
            printed = template
            return result
        }
    }

    private fun sampleTemplate() = app.devper.pharm.common.print.ReceiptTemplate(
        storeName = "Store", storeAddress = "", storePhone = "", storeTaxId = "",
        billNo = "TEST", soldAt = "-", customerName = "-", items = emptyList(),
        subtotal = 0.0, itemDiscountTotal = 0.0, cartDiscount = 0.0, total = 0.0,
        received = 0.0, change = 0.0, pharmacistName = "", footer = "",
    )

    private fun vm(repo: FakeSettingsRepository, d: app.devper.pharm.common.AppDispatchers, printer: StubPrinter = StubPrinter()) =
        SettingsEditorViewModel(
            SettingsProvider(repo),
            RefreshSettingsUseCase(repo, d),
            UpdateSettingsUseCase(repo, d),
            app.devper.pharm.domain.usecase.reports.PrintReceiptUseCase(printer, d),
        )

    @Test
    fun test_print_sends_template_to_the_printer() = runVmTest { d ->
        val printer = StubPrinter(result = true)
        val model = vm(FakeSettingsRepository(), d, printer)
        advanceUntilIdle()
        model.testPrint(sampleTemplate())
        advanceUntilIdle()
        assertEquals("TEST", printer.printed?.billNo)
        assertNull(model.state.value.errorState)
    }

    @Test
    fun test_print_failure_surfaces_error() = runVmTest { d ->
        val model = vm(FakeSettingsRepository(), d, StubPrinter(result = false))
        advanceUntilIdle()
        model.testPrint(sampleTemplate())
        advanceUntilIdle()
        assertIs<app.devper.pharm.presentation.settings.exception.SettingsUiStateError.TestPrintFailed>(model.state.value.errorState)
    }

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
    fun submit_does_not_replace_invalid_stock_values_with_defaults() = runVmTest { d ->
        val repo = FakeSettingsRepository()
        val model = vm(repo, d)
        advanceUntilIdle()
        model.onStoreName("Valid Store")
        model.onStockReorderDays("")

        assertFalse(model.state.value.canSave)
        model.submit()
        advanceUntilIdle()

        assertNull(repo.lastUpdate)
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
