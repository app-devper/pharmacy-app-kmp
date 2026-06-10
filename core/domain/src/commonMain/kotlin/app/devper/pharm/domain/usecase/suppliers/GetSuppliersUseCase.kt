package app.devper.pharm.domain.usecase.suppliers

import app.devper.pharm.domain.usecase.BaseQueryUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.repository.suppliers.SupplierRepository

class GetSuppliersUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseQueryUseCase<List<Supplier>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Supplier> = suppliers.list()
}
