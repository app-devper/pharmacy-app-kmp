package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.param.reports.ExportMovementsCsvParam
import app.devper.pharm.domain.repository.ky.ExportRepository
import app.devper.pharm.domain.export.buildCsv
import app.devper.pharm.domain.export.buildCsvBytes
import kotlinx.datetime.LocalDate

class ExportMovementsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportMovementsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportMovementsCsvParam): String {
        val filename = buildFilename(param.from, param.to, param.drugName)
        val bytes = buildCsvBytes(
            headers = param.headers.ifEmpty { listOf("at", "type", "drug", "qty", "ref", "note") },
            rows = param.rows.map { it.toCsvRow() },
        )
        return export.saveCsv(filename, bytes)
    }

    private fun StockMovement.toCsvRow(): List<Any?> = listOf(
        at,
        type.name,
        drugName,
        delta,
        reference,
        note,
    )

    private fun buildFilename(from: LocalDate?, to: LocalDate?, drugName: String): String {
        val fromTag = from?.toString().orEmpty()
        val toTag = to?.toString().orEmpty()
        val rangeTag = when {
            from == null && to == null -> "all"
            from == to                 -> fromTag.ifBlank { "all" }
            else                       -> "${fromTag.ifBlank { "any" }}_${toTag.ifBlank { "any" }}"
        }
        val drugTag = drugName.trim()
            .lowercase()
            .map { c -> if (c.isLetterOrDigit()) c else '-' }
            .joinToString("")
            .trim('-')
            .take(40)
        val drugSuffix = if (drugTag.isBlank()) "" else "_$drugTag"
        return "movements_$rangeTag$drugSuffix.csv"
    }
}
