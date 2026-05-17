package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.AddSupplierUseCase
import app.devper.pharm.domain.usecase.DeleteSupplierUseCase
import app.devper.pharm.domain.usecase.GetSuppliersUseCase
import app.devper.pharm.domain.usecase.UpdateSupplierUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val suppliersDomainModule = module {
    factoryOf(::GetSuppliersUseCase)
    factoryOf(::AddSupplierUseCase)
    factoryOf(::UpdateSupplierUseCase)
    factoryOf(::DeleteSupplierUseCase)
}
