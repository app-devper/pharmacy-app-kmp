package app.devper.pharm.domain.export

import app.devper.pharm.domain.repository.export.ExportRepository
import kotlinx.datetime.LocalDate

class CsvSheet<T>(
    val name: String,
    val defaultHeaders: List<String>,
    val cells: (T) -> List<Any?>,
)

suspend fun <T> ExportRepository.saveSheet(
    sheet: CsvSheet<T>,
    rows: List<T>,
    headers: List<String>,
    tag: String,
): String {
    val bytes = buildCsvBytes(headers = headers.ifEmpty { sheet.defaultHeaders }, rows = rows.map(sheet.cells))
    return saveCsv("${sheet.name}_$tag.csv", bytes)
}

fun periodTag(from: LocalDate?, to: LocalDate?): String {
    val fromTag = from?.toString().orEmpty()
    val toTag = to?.toString().orEmpty()
    return when {
        from == null && to == null -> "all"
        from == to -> fromTag.ifBlank { "all" }
        else -> "${fromTag.ifBlank { "any" }}_${toTag.ifBlank { "any" }}"
    }
}

fun nameTag(text: String, maxLength: Int = 40): String =
    text.trim()
        .lowercase()
        .map { c -> if (c.isLetterOrDigit()) c else '-' }
        .joinToString("")
        .trim('-')
        .take(maxLength)

fun csvMoney(value: Double): String {
    val cents = (value * 100.0 + if (value >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).let { if (it < 0) -it else it }.toString().padStart(2, '0')
    return "$whole.$frac"
}

fun csvPercent(value: Double): String = "${csvMoney(value)}%"
