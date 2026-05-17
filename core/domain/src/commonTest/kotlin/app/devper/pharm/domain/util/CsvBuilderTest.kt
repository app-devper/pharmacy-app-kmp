package app.devper.pharm.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsvBuilderTest {

    @Test
    fun empty_rows_still_emits_header_line() {
        val csv = CsvBuilder.build(headers = listOf("a", "b"), rows = emptyList())
        assertTrue(csv.contains("a,b"))
        val lines = csv.split("\r\n").filter { it.isNotEmpty() && it != BOM_ONLY }
        assertEquals(1, lines.size)
    }

    @Test
    fun rows_joined_by_crlf_line_endings() {
        val csv = CsvBuilder.build(
            headers = listOf("col1", "col2"),
            rows = listOf(listOf("a", "1"), listOf("b", "2")),
        )
        assertTrue(csv.contains("col1,col2\r\n"))
        assertTrue(csv.contains("a,1\r\n"))
        assertTrue(csv.contains("b,2\r\n"))
    }

    @Test
    fun first_byte_is_utf8_bom() {
        val bytes = CsvBuilder.buildBytes(headers = listOf("h"), rows = emptyList())
        assertEquals(0xEF.toByte(), bytes[0])
        assertEquals(0xBB.toByte(), bytes[1])
        assertEquals(0xBF.toByte(), bytes[2])
    }

    @Test
    fun field_with_comma_is_quoted() {
        assertEquals("\"a,b\"", CsvBuilder.escapeField("a,b"))
    }

    @Test
    fun field_with_double_quote_is_quoted_and_inner_quote_doubled() {
        assertEquals("\"a\"\"b\"", CsvBuilder.escapeField("a\"b"))
    }

    @Test
    fun field_with_newline_is_quoted() {
        assertEquals("\"a\nb\"", CsvBuilder.escapeField("a\nb"))
        assertEquals("\"a\rb\"", CsvBuilder.escapeField("a\rb"))
    }

    @Test
    fun plain_field_is_unchanged() {
        assertEquals("hello", CsvBuilder.escapeField("hello"))
        assertEquals("123.45", CsvBuilder.escapeField("123.45"))
        assertEquals("ภาษาไทย", CsvBuilder.escapeField("ภาษาไทย"))
    }

    @Test
    fun null_cell_renders_empty_string() {
        val csv = CsvBuilder.build(
            headers = listOf("a", "b"),
            rows = listOf(listOf("v1", null)),
        )
        assertTrue(csv.contains("v1,\r\n"))
    }

    @Test
    fun rendered_thai_text_round_trips_through_utf8() {
        val bytes = CsvBuilder.buildBytes(
            headers = listOf("ชื่อยา"),
            rows = listOf(listOf("พาราเซตามอล 500mg")),
        )
        val decoded = bytes.decodeToString()
        assertTrue(decoded.contains("ชื่อยา"))
        assertTrue(decoded.contains("พาราเซตามอล"))
    }

    @Test
    fun mixed_field_types_in_one_row() {
        val csv = CsvBuilder.build(
            headers = listOf("name", "qty", "price"),
            rows = listOf(listOf("Paracetamol", 480, 2.5)),
        )
        assertTrue(csv.contains("Paracetamol,480,2.5"))
    }

    @Test
    fun formula_starting_with_equals_is_guarded_with_tab() {
        val escaped = CsvBuilder.escapeField("=SUM(A1:A10)")
        assertEquals("\"\t=SUM(A1:A10)\"", escaped)
    }

    @Test
    fun formula_starting_with_at_sign_is_guarded() {
        val escaped = CsvBuilder.escapeField("@sum")
        assertEquals("\"\t@sum\"", escaped)
    }

    @Test
    fun negative_number_is_not_guarded() {
        assertEquals("-5.00", CsvBuilder.escapeField("-5.00"))
        assertEquals("-12", CsvBuilder.escapeField("-12"))
    }

    @Test
    fun positive_signed_number_is_not_guarded() {
        assertEquals("+5", CsvBuilder.escapeField("+5"))
    }

    @Test
    fun dash_prefix_text_is_guarded() {
        val escaped = CsvBuilder.escapeField("-cmd|/c calc")
        assertEquals("\"\t-cmd|/c calc\"", escaped)
    }

    private companion object {
        const val BOM_ONLY = "﻿"
    }
}
