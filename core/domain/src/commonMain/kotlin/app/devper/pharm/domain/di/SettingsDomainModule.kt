package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import app.devper.pharm.domain.usecase.settings.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.settings.SetDensityPreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetFontSizePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetLocalePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.SetThemePreferenceUseCase
import app.devper.pharm.domain.usecase.settings.UpdateSettingsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val settingsDomainModule = module {
    singleOf(::SettingsProvider)
    single { TimeZoneProvider { get<SettingsProvider>().state.value.timezone } }
    singleOf(::UiPreferencesProvider)
    factoryOf(::RefreshSettingsUseCase)
    factoryOf(::UpdateSettingsUseCase)
    factoryOf(::SetThemePreferenceUseCase)
    factoryOf(::SetFontSizePreferenceUseCase)
    factoryOf(::SetDensityPreferenceUseCase)
    factoryOf(::SetLocalePreferenceUseCase)
}
