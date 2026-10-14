package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.export.CsvSheet
import app.devper.pharm.domain.export.csvMoney
import app.devper.pharm.domain.export.csvPercent
import app.devper.pharm.domain.export.periodTag
import app.devper.pharm.domain.export.saveSheet
import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.param.reports.ExportProfitCsvParam
import app.devper.pharm.domain.repository.export.ExportRepository
import app.devper.pharm.domain.usecase.BaseUseCase

private val ProfitSheet = CsvSheet<DrugProfit>(
    name = "profit",
    defaultHeaders = listOf("drug", "qty", "revenue", "cost", "profit", "margin"),
) { listOf(it.drugName, it.qtySold, csvMoney(it.revenue), csvMoney(it.cost), csvMoney(it.profit), csvPercent(it.margin)) }

class ExportProfitCsvUseCase(
    private val export: ExportRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportProfitCsvParam, String>(dispatchers) {

    override suspend fun execute(param: ExportProfitCsvParam): String =
        export.saveSheet(ProfitSheet, param.rows, param.headers, periodTag(param.from, param.to))
}
