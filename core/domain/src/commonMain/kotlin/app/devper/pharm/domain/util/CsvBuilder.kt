package app.devper.pharm.domain.util

object CsvBuilder {

    private const val UTF8_BOM = "﻿"
    private const val LINE_END = "\r\n"

    fun build(headers: List<String>, rows: List<List<Any?>>): String = buildString {
        append(UTF8_BOM)
        append(headers.joinToString(",") { escapeField(it) })
        append(LINE_END)
        for (row in rows) {
            append(row.joinToString(",") { cell -> escapeField(cell?.toString().orEmpty()) })
            append(LINE_END)
        }
    }

    fun buildBytes(headers: List<String>, rows: List<List<Any?>>): ByteArray =
        build(headers, rows).encodeToByteArray()

    internal fun escapeField(value: String): String {
        val needsQuoting = value.contains(',') || value.contains('"') ||
            value.contains('\n') || value.contains('\r')
        if (!needsQuoting) return value
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}
