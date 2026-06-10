package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.sales.VoidSaleParam
import app.devper.pharm.domain.repository.sales.SaleRepository
import app.devper.pharm.domain.validation.SaleValidationError

class VoidSaleUseCase(private val sales: SaleRepository, dispatchers: AppDispatchers) : BaseUseCase<VoidSaleParam, Unit>(dispatchers) {
    override suspend fun execute(param: VoidSaleParam) {
        if (param.reason.isBlank()) throw SaleValidationError.VoidReasonRequired()
        sales.void(param)
    }
}
