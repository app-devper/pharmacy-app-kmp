package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.UpdateSupplierParam
import app.devper.pharm.domain.repository.SupplierRepository

class UpdateSupplierUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<UpdateSupplierParam, Unit>(dispatchers) {
    override suspend fun execute(param: UpdateSupplierParam) = suppliers.update(param)
}
