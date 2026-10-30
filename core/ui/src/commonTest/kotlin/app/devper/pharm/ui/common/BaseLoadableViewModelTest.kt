@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private data class DummyLoadableState(
    override val loading: Boolean = false,
    val errorState: AppException? = null,
    val value: Int = 0,
) : LoadableUiState<DummyLoadableState> {
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
    override fun withLoading(loading: Boolean) = copy(loading = loading)
}

private class DummyLoadableViewModel : BaseLoadableViewModel<DummyLoadableState>(DummyLoadableState()) {
    fun fail() = setState { withDomainError(CommonUiStateError.LoadFailed()) }

    fun read(fetch: suspend () -> Result<Int>) = load(
        fetch = fetch,
        failure = { e -> CommonUiStateError.LoadFailed(e) },
        onLoaded = { v -> copy(value = v) },
    )
}

class BaseLoadableViewModelTest {

    @Test
    fun dismissError_clears_domain_error() = runVmTest { _ ->
        val vm = DummyLoadableViewModel()
        vm.fail()
        assertIs<CommonUiStateError.LoadFailed>(vm.state.value.errorState)
        vm.dismissError()
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun load_shows_loading_then_applies_the_value() = runVmTest { _ ->
        val vm = DummyLoadableViewModel()
        val answer = CompletableDeferred<Result<Int>>()
        vm.read { answer.await() }
        assertTrue(vm.state.value.loading)
        answer.complete(Result.success(7))
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        assertEquals(7, vm.state.value.value)
    }

    @Test
    fun load_clears_the_previous_error_and_keeps_data_on_a_typed_failure() = runVmTest { _ ->
        val vm = DummyLoadableViewModel()
        vm.read { Result.success(3) }
        advanceUntilIdle()
        vm.fail()
        vm.read { Result.failure(RuntimeException("offline")) }
        assertNull(vm.state.value.errorState)
        advanceUntilIdle()
        assertIs<CommonUiStateError.LoadFailed>(vm.state.value.errorState)
        assertEquals(3, vm.state.value.value)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun a_newer_load_wins_over_an_older_one_that_answers_late() = runVmTest { _ ->
        val vm = DummyLoadableViewModel()
        val older = CompletableDeferred<Result<Int>>()
        vm.read { older.await() }
        vm.read { Result.success(2) }
        advanceUntilIdle()
        older.complete(Result.success(1))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.value)
        assertFalse(vm.state.value.loading)
    }
}
