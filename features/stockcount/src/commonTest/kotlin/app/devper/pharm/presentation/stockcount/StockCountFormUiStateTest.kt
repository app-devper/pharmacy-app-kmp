package app.devper.pharm.presentation.stockcount

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StockCountFormUiStateTest {

    private fun drug(id: String = "d1", name: String = "Paracetamol") = Drug(
        id = id, name = name,
        genericName = null, type = null, strength = null, barcode = null,
        sellPrice = Money(0.0), costPrice = Money(0.0), stock = Quantity(0), minStock = Quantity(0), unit = "ชิ้น", regNo = null,
    )

    @Test
    fun pendingLines_omits_unparseable_and_negative_values() {
        val state = StockCountFormUiState(
            drugs = listOf(drug("a"), drug("b"), drug("c"), drug("d")),
            counts = mapOf(
                "a" to "10",
                "b" to "",
                "c" to "abc",
                "d" to "-3",
            ),
        )
        assertEquals(listOf("a" to 10), state.pendingLines)
    }

    @Test
    fun pendingLines_keeps_explicit_zero() {
        val state = StockCountFormUiState(
            drugs = listOf(drug("a")),
            counts = mapOf("a" to "0"),
        )

        assertEquals(listOf("a" to 0), state.pendingLines)
    }

    @Test
    fun canSubmit_false_when_no_lines_typed() {
        val state = StockCountFormUiState()
        assertFalse(state.canSubmit)
    }

    @Test
    fun canSubmit_false_while_loading() {
        val state = StockCountFormUiState(
            loading = true,
            drugs = listOf(drug("a")),
            counts = mapOf("a" to "10"),
        )
        assertFalse(state.canSubmit)
    }

    @Test
    fun canSubmit_false_while_saving() {
        val state = StockCountFormUiState(
            saving = true,
            drugs = listOf(drug("a")),
            counts = mapOf("a" to "10"),
        )
        assertFalse(state.canSubmit)
    }

    @Test
    fun canSubmit_true_when_at_least_one_valid_line() {
        val state = StockCountFormUiState(
            drugs = listOf(drug("a")),
            counts = mapOf("a" to "10"),
        )
        assertTrue(state.canSubmit)
    }

    @Test
    fun filtered_searches_name_barcode_genericName() {
        val drugs = listOf(
            drug("1", "Paracetamol").copy(genericName = "acetaminophen", barcode = "8851001"),
            drug("2", "Ibuprofen").copy(genericName = "ibu", barcode = "8851002"),
        )
        val state = StockCountFormUiState(drugs = drugs, query = "ibu")
        assertEquals(listOf("2"), state.filtered.map { it.id })
    }
}
