package app.devper.pharm.common

import kotlin.test.Test
import kotlin.test.assertEquals

class UserMessageTest {

    @Test
    fun typed_app_exception_returns_localised_message() {
        val ex: Throwable = AuthException()
        assertEquals("กรุณาเข้าสู่ระบบใหม่", ex.userMessageOr("fallback"))
    }

    @Test
    fun non_app_exception_returns_fallback_to_avoid_leaking_internals() {
        val ex: Throwable = IllegalStateException("server stack trace leak: 10.0.0.5/users/42")
        assertEquals("fallback", ex.userMessageOr("fallback"))
    }

    @Test
    fun server_exception_message_is_safe_thai_localised() {
        val ex: Throwable = ServerException(
            message = "เซิร์ฟเวอร์ขัดข้อง (503)",
            statusCode = 503,
            body = "{\"error\":\"internal stack leak\"}",
        )
        assertEquals("เซิร์ฟเวอร์ขัดข้อง (503)", ex.userMessageOr("fallback"))
    }
}
