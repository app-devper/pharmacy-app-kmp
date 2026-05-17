package app.devper.pharm.di

import app.devper.pharm.presentation.stock.DrugFormViewModel
import app.devper.pharm.presentation.stock.DrugLotsViewModel
import app.devper.pharm.presentation.stock.StockAdjustmentsViewModel
import app.devper.pharm.presentation.stock.StockViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val stockModule = module {
    factoryOf(::StockViewModel)
    factoryOf(::DrugFormViewModel)
    factoryOf(::DrugLotsViewModel)
    factoryOf(::StockAdjustmentsViewModel)
}
