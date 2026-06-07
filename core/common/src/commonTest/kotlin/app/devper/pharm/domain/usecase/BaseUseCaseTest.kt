@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun testDispatchers(): AppDispatchers {
    val one = UnconfinedTestDispatcher()
    return AppDispatchers(main = one, io = one, default = one)
}

private class EchoUseCase(d: AppDispatchers) : BaseUseCase<Int, Int>(d) {
    override suspend fun execute(param: Int): Int = param * 2
}

private class BoomUseCase(d: AppDispatchers) : BaseUseCase<Unit, Nothing>(d) {
    override suspend fun execute(param: Unit): Nothing = throw IllegalStateException("boom")
}

private class GreetingUseCase(d: AppDispatchers) : BaseQueryUseCase<String>(d) {
    override suspend fun execute(param: Unit): String = "hello"
}

private class SquareSyncUseCase : BaseSyncUseCase<Int, Int>() {
    override fun execute(param: Int): Int = param * param
}

private class PiSyncUseCase : BaseSyncQueryUseCase<Double>() {
    override fun execute(param: Unit): Double = 3.14
}

class BaseUseCaseTest {

    @Test
    fun happy_path_returns_success() = runTest {
        val result = EchoUseCase(testDispatchers())(21)
        assertTrue(result.isSuccess)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun thrown_exception_is_captured_in_failure() = runTest {
        val result = BoomUseCase(testDispatchers())(Unit)
        assertFalse(result.isSuccess)
        val cause = result.exceptionOrNull()
        assertNotNull(cause)
        assertTrue(cause is IllegalStateException)
        assertEquals("boom", cause.message)
    }

    @Test
    fun success_result_has_no_exception() = runTest {
        val result = EchoUseCase(testDispatchers())(1)
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun base_query_usecase_can_be_invoked_without_args() = runTest {
        val greet = GreetingUseCase(testDispatchers())
        val result = greet()
        assertTrue(result.isSuccess)
        assertEquals("hello", result.getOrNull())
    }

    @Test
    fun sync_usecase_takes_param_and_returns_success() {
        val result = SquareSyncUseCase()(7)
        assertEquals(49, result.getOrNull())
    }

    @Test
    fun base_sync_query_usecase_can_be_invoked_without_args() {
        val pi = PiSyncUseCase()
        val result = pi()
        assertEquals(3.14, result.getOrNull())
    }
}
