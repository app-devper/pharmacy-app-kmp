package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.suppliers.AddSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.suppliers.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.suppliers.UpdateSupplierUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val suppliersDomainModule = module {
    factoryOf(::GetSuppliersUseCase)
    factoryOf(::AddSupplierUseCase)
    factoryOf(::UpdateSupplierUseCase)
    factoryOf(::DeleteSupplierUseCase)
}
