package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BulkImportJsonExtTest {


    @Test
    fun parses_bare_array() {
        val r = parseBulkImportJson("""[{"name":"A","sell_price":2}]""")
        val list = r.getOrThrow()
        assertEquals(1, list.size)
        assertEquals("A", list[0].name)
        assertEquals(Money(2.0), list[0].sellPrice)
    }

    @Test
    fun parses_drugs_object_wrapper() {
        val r = parseBulkImportJson("""{"drugs":[{"name":"A","sell_price":1},{"name":"B","sell_price":2}]}""")
        assertEquals(2, r.getOrThrow().size)
    }

    @Test
    fun applies_default_unit_when_blank() {
        val r = parseBulkImportJson("""[{"name":"A","sell_price":1}]""").getOrThrow()
        assertEquals("ชิ้น", r[0].unit)
    }

    @Test
    fun reads_string_price_as_number() {
        val r = parseBulkImportJson("""[{"name":"A","sell_price":"3.5","cost_price":"2"}]""").getOrThrow()
        assertEquals(Money(3.5), r[0].sellPrice)
        assertEquals(Money(2.0), r[0].costPrice)
    }

    @Test
    fun rejects_blank_input() {
        val r = parseBulkImportJson("   ")
        assertTrue(r.isFailure)
        assertNotNull(r.exceptionOrNull()?.message)
    }

    @Test
    fun rejects_string_root() {
        val r = parseBulkImportJson(""""just a string"""")
        assertTrue(r.isFailure)
    }

    @Test
    fun rejects_row_without_name() {
        val r = parseBulkImportJson("""[{"sell_price":5}]""")
        assertTrue(r.isFailure)
    }

    @Test
    fun rejects_object_without_drugs_key() {
        val r = parseBulkImportJson("""{"items":[]}""")
        assertTrue(r.isFailure)
    }

    @Test
    fun ignores_unknown_keys() {
        val r = parseBulkImportJson("""[{"name":"A","sell_price":1,"weird_key":true}]""").getOrThrow()
        assertEquals(1, r.size)
    }

    @Test
    fun parses_report_types_array() {
        val r = parseBulkImportJson("""[{"name":"A","sell_price":1,"report_types":["ky10","ky11"]}]""").getOrThrow()
        assertEquals(listOf("ky10", "ky11"), r[0].reportTypes)
    }
}
