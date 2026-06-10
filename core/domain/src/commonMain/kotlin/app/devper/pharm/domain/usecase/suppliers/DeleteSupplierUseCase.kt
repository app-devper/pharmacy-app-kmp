package app.devper.pharm.domain.usecase.suppliers

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.repository.suppliers.SupplierRepository

class DeleteSupplierUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<String, Unit>(dispatchers) {
    override suspend fun execute(param: String) = suppliers.delete(param)
}
