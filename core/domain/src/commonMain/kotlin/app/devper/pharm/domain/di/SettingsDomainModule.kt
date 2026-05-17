package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.UpdateSettingsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val settingsDomainModule = module {
    singleOf(::SettingsProvider)
    factoryOf(::RefreshSettingsUseCase)
    factoryOf(::UpdateSettingsUseCase)
}
