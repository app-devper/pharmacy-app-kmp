package app.devper.pharm.common.di

import app.devper.pharm.common.Logger
import app.devper.pharm.common.PrintlnLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    singleOf(::PrintlnLogger) bind Logger::class
}
