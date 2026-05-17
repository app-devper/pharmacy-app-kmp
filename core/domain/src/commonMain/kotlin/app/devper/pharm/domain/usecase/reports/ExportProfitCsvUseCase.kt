package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.param.ExportProfitCsvParam
import app.devper.pharm.domain.repository.ExportRepository
import app.devper.pharm.domain.util.CsvBuilder

class ExportProfitCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportProfitCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportProfitCsvParam): String {
        val filename = buildFilename(param.from, param.to)
        val bytes = CsvBuilder.buildBytes(
            headers = listOf("ชื่อยา", "จำนวนขาย", "รายได้", "ต้นทุน", "กำไร", "Margin %"),
            rows = param.rows.map { it.toCsvRow() },
        )
        return export.saveCsv(filename, bytes)
    }

    private fun DrugProfit.toCsvRow(): List<Any?> = listOf(
        drugName,
        qtySold,
        formatMoney(revenue),
        formatMoney(cost),
        formatMoney(profit),
        formatPercent(margin),
    )

    private fun formatMoney(v: Double): String {
        val cents = (v * 100.0 + if (v >= 0) 0.5 else -0.5).toLong()
        val whole = cents / 100
        val frac = (cents % 100).let { if (it < 0) -it else it }.toString().padStart(2, '0')
        return "$whole.$frac"
    }

    private fun formatPercent(v: Double): String {
        val rounded = ((v * 100.0) + if (v >= 0) 0.5 else -0.5).toLong() / 100.0
        val cents = (rounded * 100.0 + 0.5).toLong()
        val whole = cents / 100
        val frac = (cents % 100).toString().padStart(2, '0')
        return "$whole.$frac%"
    }

    private fun buildFilename(from: String, to: String): String {
        val tag = when {
            from.isBlank() && to.isBlank() -> "all"
            from == to                     -> from.ifBlank { "all" }
            else                           -> "${from.ifBlank { "any" }}_${to.ifBlank { "any" }}"
        }
        return "profit_$tag.csv"
    }
}
