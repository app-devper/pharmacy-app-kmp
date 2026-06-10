package app.devper.pharm.domain.usecase.suppliers

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.suppliers.UpdateSupplierParam
import app.devper.pharm.domain.repository.suppliers.SupplierRepository

class UpdateSupplierUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<UpdateSupplierParam, Unit>(dispatchers) {
    override suspend fun execute(param: UpdateSupplierParam) = suppliers.update(param.id, param.input)
}
