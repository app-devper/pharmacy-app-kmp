package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.extension.newClientRequestId
import app.devper.pharm.domain.param.sales.SubmitReturnParam
import app.devper.pharm.domain.repository.sales.SaleHistoryRepository
import app.devper.pharm.domain.validation.SaleValidationError

/**
 * Submits a Return as a Commercial command (ADR-0002): the request id is
 * created for a new return and reused when the same return is retried, so
 * pharmacy-api records it once even if a response was lost.
 */
class SubmitSaleReturnUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SubmitReturnParam, Unit>(dispatchers) {

    private data class Attempt(val request: SubmitReturnParam, val clientRequestId: String)

    private var pendingAttempt: Attempt? = null

    override suspend fun execute(param: SubmitReturnParam) {
        if (param.reason.isBlank()) throw SaleValidationError.ReturnReasonRequired()
        val nonZero = param.items.filter { it.qty > 0 }
        if (nonZero.isEmpty()) throw SaleValidationError.ReturnItemsRequired()

        val request = param.copy(items = nonZero, clientRequestId = null)
        val requestId = pendingAttempt?.takeIf { it.request == request }?.clientRequestId ?: newClientRequestId()
        pendingAttempt = Attempt(request, requestId)
        repo.submitReturn(request.copy(clientRequestId = requestId))
        pendingAttempt = null
    }
}
