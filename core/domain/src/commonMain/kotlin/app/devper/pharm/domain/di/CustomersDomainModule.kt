package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.customers.AddCustomerUseCase
import app.devper.pharm.domain.usecase.customers.GetCustomerSalesUseCase
import app.devper.pharm.domain.usecase.customers.GetCustomersUseCase
import app.devper.pharm.domain.usecase.customers.UpdateCustomerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val customersDomainModule = module {
    factoryOf(::GetCustomersUseCase)
    factoryOf(::AddCustomerUseCase)
    factoryOf(::UpdateCustomerUseCase)
    factoryOf(::GetCustomerSalesUseCase)
}
