package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.VoidSaleParam
import app.devper.pharm.domain.repository.SaleRepository

class VoidSaleUseCase(private val sales: SaleRepository, dispatchers: AppDispatchers) : BaseUseCase<VoidSaleParam, Unit>(dispatchers) {
    override suspend fun execute(param: VoidSaleParam) {
        require(param.reason.isNotBlank()) { "กรุณาระบุเหตุผลการยกเลิก" }
        sales.void(param)
    }
}
