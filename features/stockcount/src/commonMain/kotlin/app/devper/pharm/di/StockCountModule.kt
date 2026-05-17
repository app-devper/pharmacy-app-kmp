package app.devper.pharm.di

import app.devper.pharm.presentation.stockcount.StockCountFormViewModel
import app.devper.pharm.presentation.stockcount.StockCountsListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val stockCountModule = module {
    factoryOf(::StockCountsListViewModel)
    factoryOf(::StockCountFormViewModel)
}
