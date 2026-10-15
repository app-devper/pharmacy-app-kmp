package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
import app.devper.pharm.domain.usecase.inventory.PrintLabelsUseCase
import app.devper.pharm.domain.observer.PurchaseDraftProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val inventoryDomainModule = module {
    singleOf(::PurchaseDraftProvider)
    factoryOf(::GetReorderSuggestionsUseCase)
    factoryOf(::PrintLabelsUseCase)
}
