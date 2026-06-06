package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugListExtTest {

    private fun drug(
        id: String,
        name: String = id,
        genericName: String? = null,
        barcode: String? = null,
    ) = Drug(
        id = id, name = name, genericName = genericName, type = null, strength = null,
        barcode = barcode, sellPrice = 1.0, costPrice = 0.0, stock = 0, minStock = 0,
        unit = "ชิ้น", regNo = null,
    )

    @Test
    fun blank_query_returns_full_list() {
        val drugs = listOf(drug("a"), drug("b"))
        assertEquals(drugs, drugs.searchByQuery("  "))
    }

    @Test
    fun matches_by_name_case_insensitive() {
        val drugs = listOf(drug("1", name = "Paracetamol"), drug("2", name = "Ibuprofen"))
        assertEquals(listOf("1"), drugs.searchByQuery("PARA").map { it.id })
    }

    @Test
    fun matches_by_genericName() {
        val drugs = listOf(
            drug("1", name = "Tylenol", genericName = "acetaminophen"),
            drug("2", name = "Advil", genericName = "ibuprofen"),
        )
        assertEquals(listOf("1"), drugs.searchByQuery("acet").map { it.id })
    }

    @Test
    fun matches_by_barcode() {
        val drugs = listOf(
            drug("1", barcode = "8851001"),
            drug("2", barcode = "8851002"),
        )
        assertEquals(listOf("2"), drugs.searchByQuery("1002").map { it.id })
    }

    @Test
    fun trims_whitespace_from_query() {
        val drugs = listOf(drug("1", name = "Paracetamol"))
        assertEquals(listOf("1"), drugs.searchByQuery("  para  ").map { it.id })
    }

    @Test
    fun returns_empty_when_no_match() {
        val drugs = listOf(drug("1", name = "Paracetamol"))
        assertEquals(emptyList(), drugs.searchByQuery("nonexistent"))
    }

    @Test
    fun ranks_exact_then_prefix_then_substring() {
        val drugs = listOf(
            drug("sub", name = "Extra Para Plus"),
            drug("exact", name = "Para"),
            drug("prefix", name = "Paracetamol"),
        )
        assertEquals(listOf("exact", "prefix", "sub"), drugs.searchByQuery("para").map { it.id })
    }

    @Test
    fun ranks_exact_barcode_above_name_substring() {
        val drugs = listOf(
            drug("name", name = "888 Tablets"),
            drug("bc", name = "Zzz", barcode = "888"),
        )
        assertEquals(listOf("bc", "name"), drugs.searchByQuery("888").map { it.id })
    }
}
