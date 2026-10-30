package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.export.CsvSheet
import app.devper.pharm.domain.export.saveSheet
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.param.reports.ExportExpiringLotsCsvParam
import app.devper.pharm.domain.repository.export.ExportRepository
import app.devper.pharm.domain.usecase.BaseUseCase

private val ExpiringLotsSheet = CsvSheet<ExpiringLot>(
    name = "expiry",
    defaultHeaders = listOf("drug", "lot", "expiry", "remaining", "days_left"),
) { listOf(it.drugName, it.lotNumber, it.expiryDate, it.remaining, it.daysLeft) }

class ExportExpiringLotsCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportExpiringLotsCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportExpiringLotsCsvParam): String =
        export.saveSheet(ExpiringLotsSheet, param.rows, param.headers, param.windowTag.lowercase())
}
