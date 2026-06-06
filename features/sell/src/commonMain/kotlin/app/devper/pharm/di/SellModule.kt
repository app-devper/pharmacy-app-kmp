package app.devper.pharm.di

import app.devper.pharm.presentation.sell.SellViewModel
import app.devper.pharm.presentation.sell.flow.CheckoutViewModel
import app.devper.pharm.presentation.sell.flow.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.flow.DrugPickerViewModel
import app.devper.pharm.presentation.sell.flow.ParkedCartViewModel
import app.devper.pharm.presentation.sell.flow.VoidSaleViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val sellModule = module {
    factoryOf(::SellViewModel)
    factoryOf(::CheckoutViewModel)
    factoryOf(::DrugPickerViewModel)
    factoryOf(::CustomerPickerViewModel)
    factoryOf(::ParkedCartViewModel)
    factoryOf(::VoidSaleViewModel)
}
