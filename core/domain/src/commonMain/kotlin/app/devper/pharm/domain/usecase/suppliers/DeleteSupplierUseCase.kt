package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.SupplierRepository

class DeleteSupplierUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, Unit>(dispatchers) {
    override suspend fun execute(param: String) = suppliers.delete(param)
}
