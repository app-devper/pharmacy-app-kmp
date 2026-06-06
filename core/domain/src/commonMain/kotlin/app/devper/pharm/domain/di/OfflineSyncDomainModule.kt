package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.OfflineAutoSync
import app.devper.pharm.domain.observer.OfflineQueueProvider
import app.devper.pharm.domain.usecase.EnqueueOfflineSaleUseCase
import app.devper.pharm.domain.usecase.MarkOfflineSaleFailedUseCase
import app.devper.pharm.domain.usecase.MarkOfflineSaleSyncedUseCase
import app.devper.pharm.domain.usecase.RetryOfflineSaleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val offlineSyncDomainModule = module {
    singleOf(::OfflineQueueProvider)
    singleOf(::OfflineAutoSync)
    factoryOf(::EnqueueOfflineSaleUseCase)
    factoryOf(::MarkOfflineSaleFailedUseCase)
    factoryOf(::MarkOfflineSaleSyncedUseCase)
    factoryOf(::RetryOfflineSaleUseCase)
}
