package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.Drug
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugSearchTest {

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
        assertEquals(drugs, DrugSearch.filter(drugs, "  "))
    }

    @Test
    fun matches_by_name_case_insensitive() {
        val drugs = listOf(drug("1", name = "Paracetamol"), drug("2", name = "Ibuprofen"))
        assertEquals(listOf("1"), DrugSearch.filter(drugs, "PARA").map { it.id })
    }

    @Test
    fun matches_by_genericName() {
        val drugs = listOf(
            drug("1", name = "Tylenol", genericName = "acetaminophen"),
            drug("2", name = "Advil", genericName = "ibuprofen"),
        )
        assertEquals(listOf("1"), DrugSearch.filter(drugs, "acet").map { it.id })
    }

    @Test
    fun matches_by_barcode() {
        val drugs = listOf(
            drug("1", barcode = "8851001"),
            drug("2", barcode = "8851002"),
        )
        assertEquals(listOf("2"), DrugSearch.filter(drugs, "1002").map { it.id })
    }

    @Test
    fun trims_whitespace_from_query() {
        val drugs = listOf(drug("1", name = "Paracetamol"))
        assertEquals(listOf("1"), DrugSearch.filter(drugs, "  para  ").map { it.id })
    }

    @Test
    fun returns_empty_when_no_match() {
        val drugs = listOf(drug("1", name = "Paracetamol"))
        assertEquals(emptyList(), DrugSearch.filter(drugs, "nonexistent"))
    }
}
