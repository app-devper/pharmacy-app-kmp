package app.devper.pharm.di

import app.devper.pharm.presentation.help.HelpViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val helpModule = module {
    factoryOf(::HelpViewModel)
}
