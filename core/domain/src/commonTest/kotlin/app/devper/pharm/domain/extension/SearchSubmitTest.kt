package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Drug
import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchSubmitTest {

    private fun drug(id: String, name: String, barcode: String? = null) = Drug(
        id = id,
        name = name,
        genericName = null,
        type = null,
        strength = null,
        barcode = barcode,
        sellPrice = Money(10.0),
        costPrice = Money(5.0),
        stock = Quantity(10),
        minStock = Quantity.Zero,
        unit = null,
        regNo = null,
    )

    @Test
    fun blank_query_resolves_to_none() {
        val result = listOf(drug("a", "Amlodipine 5mg")).resolveSearchSubmit("  ")
        assertIs<SearchSubmitAction.None>(result)
    }

    @Test
    fun empty_results_resolve_to_none() {
        val result = emptyList<Drug>().resolveSearchSubmit("amlo")
        assertIs<SearchSubmitAction.None>(result)
    }

    @Test
    fun exact_barcode_match_adds_immediately_even_with_many_results() {
        val target = drug("b", "Amlodipine 10mg", barcode = "8850123456789")
        val result = listOf(drug("a", "Amlodipine 5mg"), target, drug("c", "Atorvastatin 20mg"))
            .resolveSearchSubmit("8850123456789")
        assertEquals(SearchSubmitAction.AddNow(target), result)
    }

    @Test
    fun barcode_match_is_case_insensitive_and_trimmed() {
        val target = drug("a", "Vitamin C", barcode = "ABC123")
        val result = listOf(target, drug("b", "Vitamin B")).resolveSearchSubmit(" abc123 ")
        assertEquals(SearchSubmitAction.AddNow(target), result)
    }

    @Test
    fun single_result_adds_immediately() {
        val target = drug("a", "Amlodipine 5mg")
        val result = listOf(target).resolveSearchSubmit("amlodipine 5")
        assertEquals(SearchSubmitAction.AddNow(target), result)
    }

    @Test
    fun ambiguous_results_require_confirmation_of_first() {
        val first = drug("a", "Amlodipine 5mg")
        val result = listOf(first, drug("b", "Amlodipine 10mg")).resolveSearchSubmit("amlo")
        assertEquals(SearchSubmitAction.Confirm(first), result)
    }
}
