package app.devper.pharm.di

import app.devper.pharm.presentation.expiry.ExpiryViewModel
import app.devper.pharm.presentation.labels.LabelPrintViewModel
import app.devper.pharm.presentation.planning.LowStockViewModel
import app.devper.pharm.presentation.planning.ReorderSuggestionsViewModel
import app.devper.pharm.presentation.stock.DrugFormViewModel
import app.devper.pharm.presentation.stock.DrugLotsViewModel
import app.devper.pharm.presentation.stock.StockAdjustmentsViewModel
import app.devper.pharm.presentation.stock.StockViewModel
import app.devper.pharm.presentation.stockcount.StockCountFormViewModel
import app.devper.pharm.presentation.stockcount.StockCountsListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val inventoryModule = module {
    factoryOf(::StockViewModel)
    factoryOf(::DrugFormViewModel)
    factoryOf(::DrugLotsViewModel)
    factoryOf(::StockAdjustmentsViewModel)
    factoryOf(::StockCountsListViewModel)
    factoryOf(::StockCountFormViewModel)
    factoryOf(::ExpiryViewModel)
    factoryOf(::LowStockViewModel)
    factoryOf(::ReorderSuggestionsViewModel)
    factoryOf(::LabelPrintViewModel)
}
