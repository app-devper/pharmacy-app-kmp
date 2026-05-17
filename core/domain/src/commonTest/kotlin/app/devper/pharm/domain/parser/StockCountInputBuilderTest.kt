package app.devper.pharm.domain.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StockCountInputBuilderTest {

    @Test
    fun parsePending_filters_blank_unparseable_negative() {
        val pending = StockCountInputBuilder.parsePending(
            mapOf("a" to "10", "b" to "", "c" to "abc", "d" to "-3"),
        )
        assertEquals(listOf("a" to 10), pending)
    }

    @Test
    fun parsePending_keeps_explicit_zero() {
        val pending = StockCountInputBuilder.parsePending(mapOf("a" to "0"))
        assertEquals(listOf("a" to 0), pending)
    }

    @Test
    fun build_round_trips_to_typed_lines() {
        val lines = StockCountInputBuilder.build(mapOf("a" to "10", "b" to "0"))
        assertEquals(2, lines.size)
        assertEquals("a", lines[0].drugId)
        assertEquals(10, lines[0].counted)
        assertEquals("b", lines[1].drugId)
        assertEquals(0, lines[1].counted)
    }

    @Test
    fun build_empty_when_no_valid_inputs() {
        val lines = StockCountInputBuilder.build(mapOf("a" to "", "b" to "abc"))
        assertTrue(lines.isEmpty())
    }
}
