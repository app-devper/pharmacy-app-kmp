package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.ExportMovementsCsvUseCase
import app.devper.pharm.domain.usecase.ExportProfitCsvUseCase
import app.devper.pharm.domain.usecase.GetDashboardUseCase
import app.devper.pharm.domain.usecase.GetEodReportUseCase
import app.devper.pharm.domain.usecase.GetMovementsUseCase
import app.devper.pharm.domain.usecase.GetProfitReportUseCase
import app.devper.pharm.domain.usecase.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.GetTopDrugsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val reportsDomainModule = module {
    factoryOf(::GetDashboardUseCase)
    factoryOf(::GetTopDrugsUseCase)
    factoryOf(::GetSlowDrugsUseCase)
    factoryOf(::GetProfitReportUseCase)
    factoryOf(::GetEodReportUseCase)
    factoryOf(::GetMovementsUseCase)
    factoryOf(::ExportProfitCsvUseCase)
    factoryOf(::ExportMovementsCsvUseCase)
}
