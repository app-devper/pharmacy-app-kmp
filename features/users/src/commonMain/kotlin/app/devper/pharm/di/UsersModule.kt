package app.devper.pharm.di

import app.devper.pharm.presentation.users.UserFormViewModel
import app.devper.pharm.presentation.users.UsersListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val usersModule = module {
    factoryOf(::UsersListViewModel)
    factoryOf(::UserFormViewModel)
}
