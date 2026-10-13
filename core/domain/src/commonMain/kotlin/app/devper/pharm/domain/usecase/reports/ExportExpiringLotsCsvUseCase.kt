package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.export.buildCsvBytes
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.param.reports.ExportExpiringLotsCsvParam
import app.devper.pharm.domain.repository.ky.ExportRepository
import app.devper.pharm.domain.usecase.BaseUseCase

class ExportExpiringLotsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportExpiringLotsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportExpiringLotsCsvParam): String {
        val bytes = buildCsvBytes(
            headers = param.headers.ifEmpty { listOf("drug", "lot", "expiry", "remaining", "days_left") },
            rows = param.rows.map { it.toCsvRow() },
        )
        return export.saveCsv("expiry_${param.windowTag.lowercase()}.csv", bytes)
    }

    private fun ExpiringLot.toCsvRow(): List<Any?> = listOf(drugName, lotNumber, expiryDate, remaining, daysLeft)
}
