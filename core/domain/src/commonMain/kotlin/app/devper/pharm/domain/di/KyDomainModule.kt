package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.ky.AddKy10UseCase
import app.devper.pharm.domain.usecase.ky.AddKy11UseCase
import app.devper.pharm.domain.usecase.ky.AddKy12UseCase
import app.devper.pharm.domain.usecase.ky.AddKy9UseCase
import app.devper.pharm.domain.usecase.ky.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.ky.GetKy10EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy11EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy12EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy9EntriesUseCase
import app.devper.pharm.domain.usecase.ky.SubmitKyFormsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val kyDomainModule = module {
    factoryOf(::GetKy9EntriesUseCase)
    factoryOf(::AddKy9UseCase)
    factoryOf(::GetKy10EntriesUseCase)
    factoryOf(::GetKy11EntriesUseCase)
    factoryOf(::GetKy12EntriesUseCase)
    factoryOf(::AddKy10UseCase)
    factoryOf(::AddKy11UseCase)
    factoryOf(::AddKy12UseCase)
    factoryOf(::ExportKyFormUseCase)
    factoryOf(::SubmitKyFormsUseCase)
}
