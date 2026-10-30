package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.export.CsvSheet
import app.devper.pharm.domain.export.nameTag
import app.devper.pharm.domain.export.periodTag
import app.devper.pharm.domain.export.saveSheet
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.param.reports.ExportMovementsCsvParam
import app.devper.pharm.domain.repository.export.ExportRepository
import app.devper.pharm.domain.usecase.BaseUseCase

private val MovementsSheet = CsvSheet<StockMovement>(
    name = "movements",
    defaultHeaders = listOf("at", "type", "drug", "qty", "ref", "note"),
) { listOf(it.at, it.type.name, it.drugName, it.delta, it.reference, it.note) }

class ExportMovementsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportMovementsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportMovementsCsvParam): String {
        val drug = nameTag(param.drugName)
        val tag = periodTag(param.from, param.to) + if (drug.isBlank()) "" else "_$drug"
        return export.saveSheet(MovementsSheet, param.rows, param.headers, tag)
    }
}
