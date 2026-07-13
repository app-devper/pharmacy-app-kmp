package app.devper.pharm.presentation.sell

import kotlin.test.Test
import kotlin.test.assertEquals

class CashQuickAmountsTest {

    @Test
    fun zero_total_offers_nothing() {
        assertEquals(emptyList(), cashQuickAmounts(0.0))
    }

    @Test
    fun small_total_offers_each_note() {
        assertEquals(listOf(100, 500, 1000), cashQuickAmounts(78.0))
    }

    @Test
    fun mid_total_rounds_up_per_note() {
        assertEquals(listOf(400, 500, 1000), cashQuickAmounts(347.0))
    }

    @Test
    fun note_boundary_deduplicates() {
        assertEquals(listOf(500, 1000), cashQuickAmounts(500.0))
    }

    @Test
    fun total_above_largest_note_still_offers_roundups() {
        assertEquals(listOf(1300, 1500, 2000), cashQuickAmounts(1250.0))
    }

    @Test
    fun fractional_total_rounds_up() {
        assertEquals(listOf(400, 500, 1000), cashQuickAmounts(347.50))
    }
}
