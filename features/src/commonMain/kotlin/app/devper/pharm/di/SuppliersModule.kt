package app.devper.pharm.di

import app.devper.pharm.presentation.suppliers.SupplierFormViewModel
import app.devper.pharm.presentation.suppliers.SuppliersListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val suppliersModule = module {
    factoryOf(::SuppliersListViewModel)
    factoryOf(::SupplierFormViewModel)
}
