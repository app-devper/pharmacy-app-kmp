package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.PurchaseDraftLine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PurchaseDraftProviderTest {

    private fun line(id: String) = PurchaseDraftLine(id, "Drug $id", 10, 4.0, 8.0)

    @Test
    fun add_unique_skips_duplicate_drug_ids() {
        val provider = PurchaseDraftProvider()
        provider.addUnique(listOf(line("a"), line("b")))
        provider.addUnique(listOf(line("b"), line("c")))
        assertEquals(listOf("a", "b", "c"), provider.state.value.map { it.drugId })
    }

    @Test
    fun consume_returns_and_clears() {
        val provider = PurchaseDraftProvider()
        provider.addUnique(listOf(line("a")))
        val consumed = provider.consume()
        assertEquals(listOf("a"), consumed.map { it.drugId })
        assertTrue(provider.state.value.isEmpty())
    }

    @Test
    fun clear_empties_the_draft() {
        val provider = PurchaseDraftProvider()
        provider.addUnique(listOf(line("a"), line("b")))
        provider.clear()
        assertTrue(provider.state.value.isEmpty())
    }
}
