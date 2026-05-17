package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.GetProfileUseCase
import app.devper.pharm.domain.usecase.UpdateProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val profileDomainModule = module {
    factoryOf(::GetProfileUseCase)
    factoryOf(::UpdateProfileUseCase)
    factoryOf(::ChangePasswordUseCase)
}
