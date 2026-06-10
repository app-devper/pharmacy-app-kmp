package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.reports.CloseEodUseCase
import app.devper.pharm.domain.usecase.reports.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.reports.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.reports.GetDashboardUseCase
import app.devper.pharm.domain.usecase.reports.GetEodReportUseCase
import app.devper.pharm.domain.usecase.reports.GetMovementsUseCase
import app.devper.pharm.domain.usecase.reports.GetProfitReportUseCase
import app.devper.pharm.domain.usecase.reports.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.reports.GetTopDrugsUseCase
import app.devper.pharm.domain.usecase.reports.PrintReceiptUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val reportsDomainModule = module {
    factoryOf(::GetDashboardUseCase)
    factoryOf(::GetTopDrugsUseCase)
    factoryOf(::GetSlowDrugsUseCase)
    factoryOf(::GetProfitReportUseCase)
    factoryOf(::GetEodReportUseCase)
    factoryOf(::CloseEodUseCase)
    factoryOf(::PrintReceiptUseCase)
    factoryOf(::GetMovementsUseCase)
    factoryOf(::ExportProfitCsvUseCase)
    factoryOf(::ExportMovementsCsvUseCase)
}
