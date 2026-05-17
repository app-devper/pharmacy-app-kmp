package app.devper.pharm.di

import app.devper.pharm.presentation.reports.EodViewModel
import app.devper.pharm.presentation.reports.ProfitViewModel
import app.devper.pharm.presentation.reports.ReportsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val reportsModule = module {
    factoryOf(::ReportsViewModel)
    factoryOf(::ProfitViewModel)
    factoryOf(::EodViewModel)
}
