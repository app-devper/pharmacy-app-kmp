package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.SetDensityPreferenceUseCase
import app.devper.pharm.domain.usecase.SetFontSizePreferenceUseCase
import app.devper.pharm.domain.usecase.SetThemePreferenceUseCase
import app.devper.pharm.domain.usecase.UpdateSettingsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val settingsDomainModule = module {
    singleOf(::SettingsProvider)
    singleOf(::UiPreferencesProvider)
    factoryOf(::RefreshSettingsUseCase)
    factoryOf(::UpdateSettingsUseCase)
    factoryOf(::SetThemePreferenceUseCase)
    factoryOf(::SetFontSizePreferenceUseCase)
    factoryOf(::SetDensityPreferenceUseCase)
}
