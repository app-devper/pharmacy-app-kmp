package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.param.AddSupplierParam
import app.devper.pharm.domain.repository.SupplierRepository

class AddSupplierUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<AddSupplierParam, Supplier>(dispatchers) {
    override suspend fun execute(param: AddSupplierParam): Supplier = suppliers.add(param)
}
