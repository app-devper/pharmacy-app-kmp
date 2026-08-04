package app.devper.pharm.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeZoneValidationTest {

    @Test
    fun accepts_blank_and_known_iana_timezones() {
        assertTrue("".isValidTimeZoneId())
        assertTrue("Asia/Bangkok".isValidTimeZoneId())
        assertTrue(" Europe/Berlin ".isValidTimeZoneId())
    }

    @Test
    fun rejects_unknown_timezone_names() {
        assertFalse("Bangkok".isValidTimeZoneId())
        assertFalse("Asia/Unknown_City".isValidTimeZoneId())
    }
}
