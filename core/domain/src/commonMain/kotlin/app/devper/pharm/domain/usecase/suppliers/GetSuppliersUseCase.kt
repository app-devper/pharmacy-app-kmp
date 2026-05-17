package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.domain.repository.SupplierRepository

class GetSuppliersUseCase(private val suppliers: SupplierRepository, dispatchers: AppDispatchers) :
    BaseUseCase<Unit, List<Supplier>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Supplier> = suppliers.list()
    suspend operator fun invoke(): Result<List<Supplier>> = invoke(Unit)
}
