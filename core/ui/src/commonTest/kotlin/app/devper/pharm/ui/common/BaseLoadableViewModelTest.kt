@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.ui.common

import app.devper.pharm.common.AppException
import app.devper.pharm.common.error.CommonUiStateError
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

private data class DummyLoadableState(
    override val loading: Boolean = false,
    val errorState: AppException? = null,
    val value: Int = 0,
) : LoadableUiState<DummyLoadableState> {
    override val domainError: AppException? get() = errorState
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}

private class DummyLoadableViewModel : BaseLoadableViewModel<DummyLoadableState>(DummyLoadableState()) {
    fun fail() = setState { withDomainError(CommonUiStateError.LoadFailed()) }
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
}
