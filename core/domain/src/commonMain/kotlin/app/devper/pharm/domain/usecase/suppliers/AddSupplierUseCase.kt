package app.devper.pharm.domain.usecase.suppliers

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.suppliers.SupplierInput
import app.devper.pharm.domain.repository.suppliers.SupplierRepository

class AddSupplierUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SupplierInput, Supplier>(dispatchers) {
    override suspend fun execute(param: SupplierInput): Supplier = suppliers.add(param)
}
