package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.repository.ExportRepository

class ExportKyFormUseCase(private val repo: ExportRepository, dispatchers: AppDispatchers) :
    BaseUseCase<ExportKyFormParam, String>(dispatchers) {
    override suspend fun execute(param: ExportKyFormParam): String = repo.exportKyForm(param)
}
