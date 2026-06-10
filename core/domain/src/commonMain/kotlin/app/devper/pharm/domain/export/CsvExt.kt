package app.devper.pharm.domain.export

private const val UTF8_BOM = "﻿"
private const val LINE_END = "\r\n"
private const val FORMULA_GUARD = '\t'

fun buildCsv(headers: List<String>, rows: List<List<Any?>>): String = buildString {
    append(UTF8_BOM)
    append(headers.joinToString(",") { it.escapeCsvField() })
    append(LINE_END)
    for (row in rows) {
        append(row.joinToString(",") { cell -> cell?.toString().orEmpty().escapeCsvField() })
        append(LINE_END)
    }
}

fun buildCsvBytes(headers: List<String>, rows: List<List<Any?>>): ByteArray =
    buildCsv(headers, rows).encodeToByteArray()

internal fun String.escapeCsvField(): String {
    val guarded = if (needsFormulaGuard()) "$FORMULA_GUARD$this" else this
    val needsQuoting = guarded.contains(',') || guarded.contains('"') ||
        guarded.contains('\n') || guarded.contains('\r') || guarded.contains(FORMULA_GUARD)
    if (!needsQuoting) return guarded
    val escaped = guarded.replace("\"", "\"\"")
    return "\"$escaped\""
}

private fun String.needsFormulaGuard(): Boolean {
    if (isEmpty()) return false
    return when (this[0]) {
        '=', '@' -> true
        '+', '-' -> length > 1 && !this[1].isDigit()
        else     -> false
    }
}
