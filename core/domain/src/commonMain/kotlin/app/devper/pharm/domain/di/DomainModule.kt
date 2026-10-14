package app.devper.pharm.domain.di

import org.koin.dsl.module

val domainModule = module {
    includes(
        authDomainModule,
        inventoryDomainModule,
        kyDomainModule,
        offlineSyncDomainModule,
        profileDomainModule,
        purchasingDomainModule,
        reportsDomainModule,
        salesDomainModule,
        settingsDomainModule,
        usersDomainModule,
    )
}
