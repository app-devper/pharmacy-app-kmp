package app.devper.pharm.domain.di

import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.observer.UiPreferencesProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val settingsDomainModule = module {
    singleOf(::SettingsProvider)
    single { TimeZoneProvider { get<SettingsProvider>().state.value.timezone } }
    singleOf(::UiPreferencesProvider)
}
