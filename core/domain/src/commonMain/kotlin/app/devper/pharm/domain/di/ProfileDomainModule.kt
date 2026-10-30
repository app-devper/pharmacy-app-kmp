package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.SignedInUser
import app.devper.pharm.domain.usecase.profile.ChangePasswordUseCase
import app.devper.pharm.domain.usecase.profile.UpdateProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val profileDomainModule = module {
    singleOf(::SignedInUser)
    factoryOf(::UpdateProfileUseCase)
    factoryOf(::ChangePasswordUseCase)
}
