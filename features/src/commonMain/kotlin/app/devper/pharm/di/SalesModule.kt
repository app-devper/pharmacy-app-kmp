package app.devper.pharm.di

import app.devper.pharm.presentation.saleshistory.SalesHistoryViewModel
import app.devper.pharm.presentation.sell.SellViewModel
import app.devper.pharm.presentation.sell.sibling.CheckoutViewModel
import app.devper.pharm.presentation.sell.sibling.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.sibling.DrugPickerViewModel
import app.devper.pharm.presentation.sell.sibling.ParkedCartViewModel
import app.devper.pharm.presentation.sell.sibling.VoidSaleViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val salesModule = module {
    factoryOf(::SellViewModel)
    factoryOf(::CheckoutViewModel)
    factoryOf(::DrugPickerViewModel)
    factoryOf(::CustomerPickerViewModel)
    factoryOf(::ParkedCartViewModel)
    factoryOf(::VoidSaleViewModel)
    factoryOf(::SalesHistoryViewModel)
}
