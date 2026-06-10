package app.devper.pharm.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AppExceptionTest {

    @Test
    fun auth_exception_default_message_and_subclass() {
        val ex = AuthException()
        assertEquals("Authentication required", ex.message)
        val parent: AppException = ex
        assertEquals(ex, parent)
        assertNull(ex.cause)
    }

    @Test
    fun forbidden_exception_default_message() {
        assertEquals("Forbidden", ForbiddenException().message)
    }

    @Test
    fun not_found_exception_default_message() {
        assertEquals("Not found", NotFoundException().message)
    }

    @Test
    fun network_exception_default_message() {
        assertEquals("Network unavailable", NetworkException().message)
    }

    @Test
    fun conflict_exception_payload_round_trips() {
        val ex = ConflictException(payload = "drug_already_exists")
        assertEquals("drug_already_exists", ex.payload)
        assertEquals("Conflict", ex.message)
    }

    @Test
    fun server_exception_carries_status_and_body() {
        val ex = ServerException(statusCode = 503, body = "{\"err\":\"upstream\"}")
        assertEquals(503, ex.statusCode)
        assertEquals("{\"err\":\"upstream\"}", ex.body)
    }

    @Test
    fun cause_propagates_to_runtime_exception() {
        val root = IllegalStateException("inner")
        val ex = AuthException(cause = root)
        assertSame(root, ex.cause)
    }

    @Test
    fun custom_message_overrides_default() {
        val ex = NotFoundException(message = "missing id=42")
        assertEquals("missing id=42", ex.message)
    }
}
