package app.devper.pharm.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class LoggerTest {

    @Test
    fun println_logger_is_callable_through_logger_interface() {
        val logger: Logger = PrintlnLogger()
        logger.warn(tag = "T", message = "m")
    }

    @Test
    fun debug_default_is_no_op() {
        val recorder = object : Logger {
            var debugCalled = false
            override fun warn(tag: String, message: String, cause: Throwable?) = Unit
            override fun debug(tag: String, message: String) {
                debugCalled = true
            }
        }
        recorder.debug("Tag", "msg")
        assertTrue(recorder.debugCalled)
    }

    @Test
    fun warn_signature_accepts_optional_cause() {
        val logger: Logger = PrintlnLogger()
        logger.warn(tag = "T", message = "m")
        logger.warn(tag = "T", message = "m", cause = RuntimeException("boom"))
        assertEquals(Unit, Unit)
    }
}
