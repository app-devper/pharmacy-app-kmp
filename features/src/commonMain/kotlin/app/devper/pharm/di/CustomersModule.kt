package app.devper.pharm.di

import app.devper.pharm.presentation.customers.CustomerDetailViewModel
import app.devper.pharm.presentation.customers.CustomerFormViewModel
import app.devper.pharm.presentation.customers.CustomersListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val customersModule = module {
    factoryOf(::CustomersListViewModel)
    factoryOf(::CustomerFormViewModel)
    factoryOf(::CustomerDetailViewModel)
}
