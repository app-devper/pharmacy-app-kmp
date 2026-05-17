package app.devper.pharm.di

import app.devper.pharm.presentation.expiry.ExpiryViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val expiryModule = module {
    factoryOf(::ExpiryViewModel)
}
