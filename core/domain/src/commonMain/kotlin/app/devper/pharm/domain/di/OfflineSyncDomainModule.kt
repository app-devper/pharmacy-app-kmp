package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.OfflineAutoSync
import app.devper.pharm.domain.pendingsales.PendingSales
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val offlineSyncDomainModule = module {
    singleOf(::PendingSales)
    singleOf(::OfflineAutoSync)
}
