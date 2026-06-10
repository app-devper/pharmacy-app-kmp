package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.repository.ReportsRepository

class GetEodReportUseCase(private val repo: ReportsRepository, dispatchers: AppDispatchers) :
    BaseUseCase<EodReportParam, EodReport>(dispatchers) {
    override suspend fun execute(param: EodReportParam): EodReport = repo.eod(param)
    suspend operator fun invoke(): Result<EodReport> = invoke(EodReportParam())
}
