package app.devper.pharm.di

import app.devper.pharm.presentation.help.HelpViewModel
import app.devper.pharm.presentation.offlinesync.OfflineSyncViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val offlineSyncModule = module {
    factoryOf(::OfflineSyncViewModel)
    factoryOf(::HelpViewModel)
}
