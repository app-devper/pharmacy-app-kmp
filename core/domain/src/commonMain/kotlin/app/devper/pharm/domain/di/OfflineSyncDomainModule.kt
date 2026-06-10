package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.OfflineAutoSync
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.usecase.offlinesync.EnqueueOfflineSaleUseCase
import app.devper.pharm.domain.usecase.offlinesync.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.domain.usecase.offlinesync.RetryOfflineSaleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val offlineSyncDomainModule = module {
    singleOf(::OfflineQueueProvider)
    singleOf(::OfflineAutoSync)
    factoryOf(::EnqueueOfflineSaleUseCase)
    factoryOf(::MarkOfflineSaleSyncedUseCase)
    factoryOf(::RetryOfflineSaleUseCase)
}
