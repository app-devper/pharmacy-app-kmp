package app.devper.pharm.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class IdentityUseCase : BaseSyncUseCase<String, String>() {
    override fun execute(param: String): String = param
}

private class FailingUseCase : BaseSyncUseCase<Unit, Nothing>() {
    override fun execute(param: Unit): Nothing = throw IllegalArgumentException("bad input")
}

class BaseSyncUseCaseTest {

    @Test
    fun happy_path_returns_success() {
        val result = IdentityUseCase()("hello")
        assertTrue(result.isSuccess)
        assertEquals("hello", result.getOrNull())
    }

    @Test
    fun thrown_exception_returns_failure() {
        val result = FailingUseCase()(Unit)
        assertFalse(result.isSuccess)
        val cause = result.exceptionOrNull()
        assertNotNull(cause)
        assertTrue(cause is IllegalArgumentException)
        assertEquals("bad input", cause.message)
    }
}
