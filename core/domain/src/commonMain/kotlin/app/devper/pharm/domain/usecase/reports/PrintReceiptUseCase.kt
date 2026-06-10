package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate

class PrintReceiptUseCase(
    private val printer: ReceiptPrinter,
    dispatchers: AppDispatchers,
) : BaseUseCase<ReceiptTemplate, Boolean>(dispatchers) {
    override suspend fun execute(param: ReceiptTemplate): Boolean = printer.print(param)
}
