package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.command.CommandAttempts
import app.devper.pharm.domain.command.CommandKind
import app.devper.pharm.domain.param.sales.SubmitReturnParam
import app.devper.pharm.domain.repository.sales.SaleHistoryRepository
import app.devper.pharm.domain.validation.SaleValidationError

class SubmitSaleReturnUseCase(
    private val repo: SaleHistoryRepository,
    dispatchers: AppDispatchers,
    private val attempts: CommandAttempts = CommandAttempts(),
) : BaseUseCase<SubmitReturnParam, Unit>(dispatchers) {

    override suspend fun execute(param: SubmitReturnParam) {
        if (param.reason.isBlank()) throw SaleValidationError.ReturnReasonRequired()
        val nonZero = param.items.filter { it.qty > 0 }
        if (nonZero.isEmpty()) throw SaleValidationError.ReturnItemsRequired()

        val request = param.copy(items = nonZero, clientRequestId = null)
        val requestId = attempts.idFor(CommandKind.Return, request)
        repo.submitReturn(request.copy(clientRequestId = requestId))
        attempts.settled(CommandKind.Return)
    }
}
