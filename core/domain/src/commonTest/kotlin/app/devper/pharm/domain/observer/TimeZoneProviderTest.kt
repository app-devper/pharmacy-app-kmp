package app.devper.pharm.domain.observer

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeZoneProviderTest {

    @Test
    fun returns_settings_timezone_when_valid() {
        val provider = TimeZoneProvider { "Europe/Berlin" }
        assertEquals(TimeZone.of("Europe/Berlin"), provider.current)
    }

    @Test
    fun falls_back_to_asia_bangkok_when_blank() {
        val provider = TimeZoneProvider { "" }
        assertEquals(TimeZone.of("Asia/Bangkok"), provider.current)
    }

    @Test
    fun falls_back_to_asia_bangkok_when_unknown() {
        val provider = TimeZoneProvider { "Garbage/NotAZone" }
        assertEquals(TimeZone.of("Asia/Bangkok"), provider.current)
    }

    @Test
    fun reflects_latest_settings_value_on_each_access() {
        var current = "Asia/Bangkok"
        val provider = TimeZoneProvider { current }
        assertEquals(TimeZone.of("Asia/Bangkok"), provider.current)
        current = "Europe/Berlin"
        assertEquals(TimeZone.of("Europe/Berlin"), provider.current)
    }
}
