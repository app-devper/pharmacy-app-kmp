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

    private companion object {
        const val BOM_ONLY = "﻿"
    }
}
