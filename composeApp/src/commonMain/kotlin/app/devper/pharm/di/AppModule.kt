package app.devper.pharm.di

import app.devper.pharm.common.di.commonModule
import app.devper.pharm.data.di.dataModule
import app.devper.pharm.domain.di.domainModule
import app.devper.pharm.presentation.AppViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    includes(
        commonModule,
        domainModule,
        dataModule,

        authModule,

        customersModule,
        suppliersModule,
        importsModule,
        bulkImportModule,
        kyModule,

        stockModule,
        stockCountModule,
        planningModule,
        labelsModule,
        expiryModule,

        sellModule,
        salesHistoryModule,

        reportsModule,
        movementsModule,

        settingsModule,

        offlineSyncModule,
        helpModule,

        profileModule,
        usersModule,
    )

    factoryOf(::AppViewModel)
}
