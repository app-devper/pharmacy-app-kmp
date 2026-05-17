package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.repository.ExportRepository
import app.devper.pharm.domain.util.CsvBuilder

class ExportMovementsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<List<StockMovement>, String>(dispatchers) {

    override suspend fun execute(param: List<StockMovement>): String {
        val bytes = CsvBuilder.buildBytes(
            headers = listOf("เวลา", "ประเภท", "ยา", "จำนวน", "อ้างอิง", "หมายเหตุ"),
            rows = param.map { it.toCsvRow() },
        )
        return export.saveCsv("movements.csv", bytes)
    }

    private fun StockMovement.toCsvRow(): List<Any?> = listOf(
        at,
        type.name,
        drugName,
        delta,
        reference,
        note,
    )
}
