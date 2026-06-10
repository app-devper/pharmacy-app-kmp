package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.users.CreateUserUseCase
import app.devper.pharm.domain.usecase.users.DeleteUserUseCase
import app.devper.pharm.domain.usecase.users.GetUsersUseCase
import app.devper.pharm.domain.usecase.users.SetUserPasswordUseCase
import app.devper.pharm.domain.usecase.users.SetUserRoleUseCase
import app.devper.pharm.domain.usecase.users.SetUserStatusUseCase
import app.devper.pharm.domain.usecase.users.UpdateUserUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val usersDomainModule = module {
    factoryOf(::GetUsersUseCase)
    factoryOf(::CreateUserUseCase)
    factoryOf(::UpdateUserUseCase)
    factoryOf(::DeleteUserUseCase)
    factoryOf(::SetUserRoleUseCase)
    factoryOf(::SetUserStatusUseCase)
    factoryOf(::SetUserPasswordUseCase)
}
