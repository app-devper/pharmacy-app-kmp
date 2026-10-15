package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.reports.CloseEodUseCase
import app.devper.pharm.domain.usecase.reports.ExportDrugsCsvUseCase
import app.devper.pharm.domain.usecase.reports.ExportExpiringLotsCsvUseCase
import app.devper.pharm.domain.usecase.reports.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.reports.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.reports.PrintReceiptUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val reportsDomainModule = module {
    factoryOf(::CloseEodUseCase)
    factoryOf(::PrintReceiptUseCase)
    factoryOf(::ExportProfitCsvUseCase)
    factoryOf(::ExportMovementsCsvUseCase)
    factoryOf(::ExportExpiringLotsCsvUseCase)
    factoryOf(::ExportDrugsCsvUseCase)
}
