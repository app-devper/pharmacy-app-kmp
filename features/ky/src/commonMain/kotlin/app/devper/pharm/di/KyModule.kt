package app.devper.pharm.di

import app.devper.pharm.presentation.ky.Ky9AddViewModel
import app.devper.pharm.presentation.ky.Ky9ViewModel
import app.devper.pharm.presentation.ky.KyListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val kyModule = module {
    factoryOf(::Ky9ViewModel)
    factoryOf(::Ky9AddViewModel)
    factoryOf(::KyListViewModel)
}
