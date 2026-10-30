@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateError
import app.devper.pharm.common.error.CommonUiStateMessage
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull

private data class SheetState(
    override val loading: Boolean = false,
    val errorState: AppException? = null,
    val exporting: Boolean = false,
    val messageState: CommonUiStateMessage? = null,
) : ExportableUiState<SheetState> {
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
    override fun withLoading(loading: Boolean) = copy(loading = loading)
    override fun withExporting(exporting: Boolean) = copy(exporting = exporting)
    override fun withMessage(message: CommonUiStateMessage?) = copy(messageState = message)
}

private class SheetViewModel : BaseExportableViewModel<SheetState>(SheetState()) {
    var saved: List<Int>? = null
    fun exportRows(rows: List<Int>, result: Result<String>) = export(rows) { saved = it; result }
}

class BaseExportableViewModelTest {

    @Test
    fun exporting_nothing_says_so_without_saving_a_file() = runVmTest { _ ->
        val vm = SheetViewModel()
        vm.exportRows(emptyList(), Result.success("x.csv"))
        advanceUntilIdle()
        assertEquals(CommonUiStateMessage.ExportEmpty, vm.state.value.messageState)
        assertNull(vm.saved)
    }

    @Test
    fun a_saved_file_says_where_it_went() = runVmTest { _ ->
        val vm = SheetViewModel()
        vm.exportRows(listOf(1, 2), Result.success("/tmp/x.csv"))
        advanceUntilIdle()
        assertEquals(listOf(1, 2), vm.saved)
        assertEquals(CommonUiStateMessage.ExportDone("/tmp/x.csv"), vm.state.value.messageState)
        assertFalse(vm.state.value.exporting)
        vm.dismissMessage()
        assertNull(vm.state.value.messageState)
    }

    @Test
    fun a_failed_save_is_a_typed_export_error() = runVmTest { _ ->
        val vm = SheetViewModel()
        vm.exportRows(listOf(1), Result.failure(RuntimeException("disk full")))
        advanceUntilIdle()
        assertIs<CommonUiStateError.ExportFailed>(vm.state.value.errorState)
        assertFalse(vm.state.value.exporting)
    }
}
