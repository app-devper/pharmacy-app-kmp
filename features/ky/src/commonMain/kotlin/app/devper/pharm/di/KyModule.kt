package app.devper.pharm.di

import app.devper.pharm.presentation.ky.Ky10AddViewModel
import app.devper.pharm.presentation.ky.Ky11AddViewModel
import app.devper.pharm.presentation.ky.Ky12AddViewModel
import app.devper.pharm.presentation.ky.Ky9AddViewModel
import app.devper.pharm.presentation.ky.Ky9ViewModel
import app.devper.pharm.presentation.ky.KyListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val kyModule = module {
    factoryOf(::Ky9ViewModel)
    factoryOf(::Ky9AddViewModel)
    factoryOf(::Ky10AddViewModel)
    factoryOf(::Ky11AddViewModel)
    factoryOf(::Ky12AddViewModel)
    factoryOf(::KyListViewModel)
}
