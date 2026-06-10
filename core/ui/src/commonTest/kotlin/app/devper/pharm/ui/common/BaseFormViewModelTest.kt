@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.AuthException
import app.devper.pharm.common.error.CommonUiStateError
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private data class DummyFormState(
    val name: String = "",
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<DummyFormState> {
    override val domainError: AppException? get() = errorState
    override val canSubmit: Boolean get() = name.isNotBlank() && !saving
    override fun withSaving(saving: Boolean): DummyFormState = copy(saving = saving)
    override fun withSaved(saved: Boolean): DummyFormState = copy(saved = saved)
    override fun withDomainError(error: AppException?): DummyFormState = copy(errorState = error)
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
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun submit_with_untyped_throwable_maps_to_save_failed() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.failure(IllegalStateException("server down")) })
        vm.setName("น้ำเกลือ")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saving)
        assertFalse(vm.state.value.saved)
        assertIs<CommonUiStateError.SaveFailed>(vm.state.value.errorState)
    }

    @Test
    fun submit_with_typed_AppException_passes_it_through() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.failure(AuthException()) })
        vm.setName("น้ำเกลือ")
        vm.submit()
        advanceUntilIdle()
        assertFalse(vm.state.value.saving)
        assertFalse(vm.state.value.saved)
        assertIs<AuthException>(vm.state.value.errorState)
    }

    @Test
    fun dismissError_clears_error() = runVmTest { _ ->
        val vm = DummyFormViewModel(persistResult = { Result.failure(RuntimeException("x")) })
        vm.setName("Foo")
        vm.submit()
        advanceUntilIdle()
        assertIs<CommonUiStateError.SaveFailed>(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
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
