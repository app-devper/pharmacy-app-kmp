package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.PrintLabelsParam
import app.devper.pharm.domain.repository.LabelRepository

class PrintLabelsUseCase(
    private val labels: LabelRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<PrintLabelsParam, String>(dispatchers) {

    override suspend fun execute(param: PrintLabelsParam): String =
        labels.printLabels(param)
}
