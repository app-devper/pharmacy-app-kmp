package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.labels.PrintLabelsParam
import app.devper.pharm.domain.repository.inventory.LabelRepository

class PrintLabelsUseCase(
    private val labels: LabelRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<PrintLabelsParam, String>(dispatchers) {

    override suspend fun execute(param: PrintLabelsParam): String =
        labels.printLabels(param)
}
