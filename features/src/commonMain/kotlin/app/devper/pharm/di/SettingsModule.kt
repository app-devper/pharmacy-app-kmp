package app.devper.pharm.di

import app.devper.pharm.presentation.settings.SettingsEditorViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsModule = module {
    factoryOf(::SettingsEditorViewModel)
}
