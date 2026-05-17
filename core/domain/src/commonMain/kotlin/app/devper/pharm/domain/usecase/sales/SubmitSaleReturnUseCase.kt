package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.SubmitReturnParam
import app.devper.pharm.domain.repository.SaleHistoryRepository

class SubmitSaleReturnUseCase(private val repo: SaleHistoryRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SubmitReturnParam, Unit>(dispatchers) {
    override suspend fun execute(param: SubmitReturnParam) {
        require(param.reason.isNotBlank()) { "กรุณาระบุเหตุผลการคืนสินค้า" }
        val nonZero = param.items.filter { it.qty > 0 }
        require(nonZero.isNotEmpty()) { "กรุณาเลือกอย่างน้อย 1 รายการที่จะคืน" }
        repo.submitReturn(param.copy(items = nonZero))
    }
}
