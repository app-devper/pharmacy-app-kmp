package app.devper.pharm.domain.util

object CsvBuilder {

    private const val UTF8_BOM = "﻿"
    private const val LINE_END = "\r\n"
    private const val FORMULA_GUARD = '\t'

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
        val guarded = if (needsFormulaGuard(value)) "$FORMULA_GUARD$value" else value
        val needsQuoting = guarded.contains(',') || guarded.contains('"') ||
            guarded.contains('\n') || guarded.contains('\r') || guarded.contains(FORMULA_GUARD)
        if (!needsQuoting) return guarded
        val escaped = guarded.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    private fun needsFormulaGuard(value: String): Boolean {
        if (value.isEmpty()) return false
        return when (value[0]) {
            '=', '@' -> true
            '+', '-' -> value.length > 1 && !value[1].isDigit()
            else     -> false
        }
    }
}
