package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.SubmitReturnParam
import app.devper.pharm.domain.repository.SaleHistoryRepository
import app.devper.pharm.domain.validation.SaleValidationError

class SubmitSaleReturnUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SubmitReturnParam, Unit>(dispatchers) {
    override suspend fun execute(param: SubmitReturnParam) {
        if (param.reason.isBlank()) throw SaleValidationError.ReturnReasonRequired()
        val nonZero = param.items.filter { it.qty > 0 }
        if (nonZero.isEmpty()) throw SaleValidationError.ReturnItemsRequired()
        repo.submitReturn(param.copy(items = nonZero))
    }
}
