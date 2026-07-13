package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.export.buildCsvBytes
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.reports.ExportDrugsCsvParam
import app.devper.pharm.domain.repository.ky.ExportRepository
import app.devper.pharm.domain.usecase.BaseUseCase

class ExportDrugsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportDrugsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportDrugsCsvParam): String {
        val bytes = buildCsvBytes(
            headers = param.headers.ifEmpty {
                listOf("name", "generic", "type", "barcode", "stock", "unit", "min_stock", "cost", "sell", "reg_no")
            },
            rows = param.rows.map { it.toCsvRow() },
        )
        return export.saveCsv("drugs_${param.rows.size}.csv", bytes)
    }

    private fun Drug.toCsvRow(): List<Any?> = listOf(
        name,
        genericName,
        type,
        barcode,
        stock.value,
        unit,
        minStock.value,
        costPrice.amount,
        sellPrice.amount,
        regNo,
    )
}
