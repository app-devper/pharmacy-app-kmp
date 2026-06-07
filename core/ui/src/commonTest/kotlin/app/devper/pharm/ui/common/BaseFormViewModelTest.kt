@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.error.ErrorMessages
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private data class DummyFormState(
    val name: String = "",
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val loading: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<DummyFormState> {
    override val canSubmit: Boolean get() = name.isNotBlank() && !saving
    override fun withSaving(saving: Boolean): DummyFormState = copy(saving = saving)
    override fun withSaved(saved: Boolean): DummyFormState = copy(saved = saved)
    override fun withError(error: String?): DummyFormState = copy(error = error)
}

private class DummyFormViewModel(
    private val persistResult: () -> Result<Unit>,
) : BaseFormViewModel<DummyFormState>(DummyFormState()) {
    fun setName(value: String) = setState { copy(name = value) }
    override suspend fun persist(): Result<Unit> = persistResult()
}

class BaseFormViewModelTest {

    @Test
    fun submit_is_noop_when_canSubmit_is_false() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.success(Unit) })
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saving)
        assertFalse(vm.state.value.saved)
    }

    @Test
    fun submit_sets_saving_then_saved_on_success() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.success(Unit) })
        vm.setName("น้ำเกลือ")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saving)
        assertTrue(vm.state.value.saved)
        assertNull(vm.state.value.error)
    }

    @Test
    fun submit_with_untyped_throwable_falls_back_to_default_save_failed_message() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.failure(IllegalStateException("server down")) })
        vm.setName("น้ำเกลือ")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saving)
        assertFalse(vm.state.value.saved)
        assertEquals(ErrorMessages.SAVE_FAILED, vm.state.value.error)
    }

    @Test
    fun submit_with_typed_AppException_surfaces_its_localised_message() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.failure(AuthException()) })
        vm.setName("น้ำเกลือ")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saving)
        assertFalse(vm.state.value.saved)
        val err = vm.state.value.error
        assertNotNull(err)
        assertEquals("กรุณาเข้าสู่ระบบใหม่", err)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.failure(RuntimeException("x")) })
        vm.setName("Foo")
        vm.submit()
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }

    @Test
    fun resetSaved_clears_saved_flag() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.success(Unit) })
        vm.setName("Foo")
        vm.submit()
        advanceUntilIdle()
        assertTrue(vm.state.value.saved)
        vm.resetSaved()
        assertFalse(vm.state.value.saved)
    }
}
