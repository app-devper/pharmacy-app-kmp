package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.AddCustomerUseCase
import app.devper.pharm.domain.usecase.GetCustomerSalesUseCase
import app.devper.pharm.domain.usecase.GetCustomersUseCase
import app.devper.pharm.domain.usecase.UpdateCustomerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val customersDomainModule = module {
    factoryOf(::GetCustomersUseCase)
    factoryOf(::AddCustomerUseCase)
    factoryOf(::UpdateCustomerUseCase)
    factoryOf(::GetCustomerSalesUseCase)
}
