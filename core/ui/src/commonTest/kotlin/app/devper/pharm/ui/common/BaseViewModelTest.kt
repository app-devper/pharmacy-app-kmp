@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private data class CounterState(
    override val loading: Boolean = false,
    override val error: String? = null,
    val count: Int = 0,
) : BaseUiState

private class CounterViewModel : BaseViewModel<CounterState>(CounterState()) {
    fun increment() = setState { copy(count = count + 1) }

    fun setError(message: String) = setState { copy(error = message) }

    fun load(value: Int, fail: Boolean = false) {
        launchResult(
            block = { if (fail) Result.failure(IllegalStateException("nope")) else Result.success(value) },
            onSuccess = { setState { copy(count = it) } },
            onFailure = { e -> setState { copy(error = e.message) } },
            withLoading = { l -> setState { copy(loading = l) } },
        )
    }
}

class BaseViewModelTest {

    @Test
    fun setState_updates_emitted_state() = runVmTest { _ ->
        val vm = CounterViewModel()
        assertEquals(0, vm.state.value.count)
        vm.increment()
        vm.increment()
        assertEquals(2, vm.state.value.count)
    }

    @Test
    fun launchResult_onSuccess_writes_value() = runVmTest { _ ->
        val vm = CounterViewModel()
        vm.load(value = 42)
        advanceUntilIdle()
        assertEquals(42, vm.state.value.count)
        assertNull(vm.state.value.error)
    }

    @Test
    fun launchResult_onFailure_writes_error() = runVmTest { _ ->
        val vm = CounterViewModel()
        vm.load(value = 0, fail = true)
        advanceUntilIdle()
        val err = vm.state.value.error
        assertNotNull(err)
        assertEquals("nope", err)
    }

    @Test
    fun launchResult_clears_loading_on_success() = runVmTest { _ ->
        val vm = CounterViewModel()
        vm.load(value = 7)
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun setError_action_is_observable() = runVmTest { _ ->
        val vm = CounterViewModel()
        vm.setError("boom")
        assertEquals("boom", vm.state.value.error)
        assertTrue(vm.state.value.error?.isNotEmpty() == true)
    }
}
