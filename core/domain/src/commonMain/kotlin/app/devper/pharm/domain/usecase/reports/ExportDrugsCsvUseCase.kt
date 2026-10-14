package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.export.CsvSheet
import app.devper.pharm.domain.export.saveSheet
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.reports.ExportDrugsCsvParam
import app.devper.pharm.domain.repository.export.ExportRepository
import app.devper.pharm.domain.usecase.BaseUseCase

private val DrugsSheet = CsvSheet<Drug>(
    name = "drugs",
    defaultHeaders = listOf("name", "generic", "type", "barcode", "stock", "unit", "min_stock", "cost", "sell", "reg_no"),
) {
    listOf(
        it.name, it.genericName, it.type, it.barcode, it.stock.value, it.unit,
        it.minStock.value, it.costPrice.amount, it.sellPrice.amount, it.regNo,
    )
}

class ExportDrugsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportDrugsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportDrugsCsvParam): String =
        export.saveSheet(DrugsSheet, param.rows, param.headers, param.rows.size.toString())
}
