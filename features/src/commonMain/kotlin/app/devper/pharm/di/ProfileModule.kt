package app.devper.pharm.di

import app.devper.pharm.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val profileModule = module {
    factoryOf(::ProfileViewModel)
}
