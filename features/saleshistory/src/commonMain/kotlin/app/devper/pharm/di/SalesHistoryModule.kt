package app.devper.pharm.di

import app.devper.pharm.presentation.saleshistory.SalesHistoryViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val salesHistoryModule = module {
    factoryOf(::SalesHistoryViewModel)
}
