package app.devper.pharm.ui.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

class FreshnessTest {

    @Test
    fun nothing_loaded_yet_has_no_freshness() {
        assertEquals(Freshness.Unknown, freshnessOf(lastLoadedAtMillis = null, refreshFailed = false))
        assertEquals(Freshness.Unknown, freshnessOf(lastLoadedAtMillis = null, refreshFailed = true))
    }

    @Test
    fun a_successful_load_is_confirmed_at_its_time() {
        assertEquals(Freshness.Confirmed(1_000L), freshnessOf(lastLoadedAtMillis = 1_000L, refreshFailed = false))
    }

    @Test
    fun a_failed_refresh_keeps_the_last_confirmed_time_as_stale() {
        assertEquals(Freshness.Stale(1_000L), freshnessOf(lastLoadedAtMillis = 1_000L, refreshFailed = true))
    }
}
