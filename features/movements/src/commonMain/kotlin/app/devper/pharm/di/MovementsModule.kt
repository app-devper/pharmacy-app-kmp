package app.devper.pharm.di

import app.devper.pharm.presentation.movements.MovementsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val movementsModule = module {
    factoryOf(::MovementsViewModel)
}
