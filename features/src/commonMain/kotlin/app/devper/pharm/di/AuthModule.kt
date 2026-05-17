package app.devper.pharm.di

import app.devper.pharm.presentation.auth.LoginViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authModule = module {
    factoryOf(::LoginViewModel)
}
