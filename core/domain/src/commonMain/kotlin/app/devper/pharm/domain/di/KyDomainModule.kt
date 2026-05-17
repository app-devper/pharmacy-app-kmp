package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.AddKy9UseCase
import app.devper.pharm.domain.usecase.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.GetKy10EntriesUseCase
import app.devper.pharm.domain.usecase.GetKy11EntriesUseCase
import app.devper.pharm.domain.usecase.GetKy12EntriesUseCase
import app.devper.pharm.domain.usecase.GetKy9EntriesUseCase
import app.devper.pharm.domain.usecase.SubmitKyFormsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val kyDomainModule = module {
    factoryOf(::GetKy9EntriesUseCase)
    factoryOf(::AddKy9UseCase)
    factoryOf(::GetKy10EntriesUseCase)
    factoryOf(::GetKy11EntriesUseCase)
    factoryOf(::GetKy12EntriesUseCase)
    factoryOf(::ExportKyFormUseCase)
    factoryOf(::SubmitKyFormsUseCase)
}
