package app.devper.pharm.di

import app.devper.pharm.presentation.labels.LabelPrintViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val labelsModule = module {
    factoryOf(::LabelPrintViewModel)
}
