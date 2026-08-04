package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.AuthStateProvider
import app.devper.pharm.domain.observer.SessionExpiryProvider
import app.devper.pharm.domain.usecase.auth.LoginUseCase
import app.devper.pharm.domain.usecase.auth.LogoutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDomainModule = module {
    singleOf(::AuthStateProvider)
    singleOf(::SessionExpiryProvider)
    factoryOf(::LoginUseCase)
    factoryOf(::LogoutUseCase)
}
