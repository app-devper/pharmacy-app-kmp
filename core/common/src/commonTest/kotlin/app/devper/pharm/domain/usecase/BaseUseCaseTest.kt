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
}
