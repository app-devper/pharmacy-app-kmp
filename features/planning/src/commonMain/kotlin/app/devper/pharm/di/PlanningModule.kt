package app.devper.pharm.di

import app.devper.pharm.presentation.planning.LowStockViewModel
import app.devper.pharm.presentation.planning.ReorderSuggestionsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val planningModule = module {
    factoryOf(::LowStockViewModel)
    factoryOf(::ReorderSuggestionsViewModel)
}
