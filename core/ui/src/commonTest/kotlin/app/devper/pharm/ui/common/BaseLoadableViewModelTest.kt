package app.devper.pharm.ui.common

import app.devper.pharm.common.AuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private data class StubState(
    override val loading: Boolean = false,
    override val error: String? = null,
    val items: List<String> = emptyList(),
) : LoadableUiState<StubState> {
    override fun withLoading(value: Boolean) = copy(loading = value)
    override fun withError(value: String?) = copy(error = value)
}

private class StubVm(private val source: suspend () -> Result<List<String>>) :
    BaseLoadableViewModel<StubState>(StubState()) {
    fun load() = launchLoad(
        block = source,
        onSuccess = { result -> copy(items = result) },
    )

    fun loadWithFallback(fallback: String) = launchLoad(
        block = source,
        fallback = fallback,
        onSuccess = { result -> copy(items = result) },
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseLoadableViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(kotlinx.coroutines.test.StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun success_sets_loading_false_clears_error_and_applies_payload() = runTest {
        val vm = StubVm { Result.success(listOf("a", "b")) }
        vm.load()
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.loading)
        assertNull(s.error)
        assertEquals(listOf("a", "b"), s.items)
    }

    @Test
    fun success_overwrites_previously_set_error() = runTest {
        val vm = StubVm { Result.success(emptyList()) }
        vm.state.value.withError("stale").also { /* manually seeded */ }
        vm.load()
        advanceUntilIdle()
        assertNull(vm.state.value.error)
    }

    @Test
    fun failure_with_typed_AppException_uses_its_localised_message() = runTest {
        val vm = StubVm { Result.failure(AuthException()) }
        vm.load()
        advanceUntilIdle()
        val s = vm.state.value
        assertFalse(s.loading)
        assertEquals("กรุณาเข้าสู่ระบบใหม่", s.error)
    }

    @Test
    fun failure_with_untyped_throwable_uses_default_fallback() = runTest {
        val vm = StubVm { Result.failure(IllegalStateException("internal leak")) }
        vm.load()
        advanceUntilIdle()
        assertEquals("โหลดข้อมูลไม่สำเร็จ", vm.state.value.error)
    }

    @Test
    fun failure_with_untyped_throwable_uses_supplied_fallback() = runTest {
        val vm = StubVm { Result.failure(IllegalStateException("internal leak")) }
        vm.loadWithFallback("custom fallback")
        advanceUntilIdle()
        assertEquals("custom fallback", vm.state.value.error)
    }

    @Test
    fun dismissError_clears_error_field() = runTest {
        val vm = StubVm { Result.failure(IllegalStateException("boom")) }
        vm.load()
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        vm.dismissError()
        assertNull(vm.state.value.error)
    }

    @Test
    fun loading_flag_is_true_while_block_is_pending() = runTest {
        var resolved = false
        val vm = StubVm {
            while (!resolved) kotlinx.coroutines.yield()
            Result.success(emptyList())
        }
        vm.load()
        kotlinx.coroutines.yield()
        assertTrue(vm.state.value.loading)
        resolved = true
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
    }
}
