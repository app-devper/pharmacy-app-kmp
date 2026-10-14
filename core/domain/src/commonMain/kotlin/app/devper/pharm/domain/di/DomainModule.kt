package app.devper.pharm.domain.di

import org.koin.dsl.module

val domainModule = module {
    includes(
        authDomainModule,
        inventoryDomainModule,
        offlineSyncDomainModule,
        profileDomainModule,
        reportsDomainModule,
        salesDomainModule,
        settingsDomainModule,
    )
}
