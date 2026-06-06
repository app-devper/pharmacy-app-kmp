package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.param.ExportMovementsCsvParam
import app.devper.pharm.domain.repository.ExportRepository
import app.devper.pharm.domain.extension.buildCsv
import app.devper.pharm.domain.extension.buildCsvBytes

class ExportMovementsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportMovementsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportMovementsCsvParam): String {
        val filename = buildFilename(param.from, param.to, param.drugName)
        val bytes = buildCsvBytes(
            headers = listOf("เวลา", "ประเภท", "ยา", "จำนวน", "อ้างอิง", "หมายเหตุ"),
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

    private fun buildFilename(from: String, to: String, drugName: String): String {
        val rangeTag = when {
            from.isBlank() && to.isBlank() -> "all"
            from == to                     -> from.ifBlank { "all" }
            else                           -> "${from.ifBlank { "any" }}_${to.ifBlank { "any" }}"
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
