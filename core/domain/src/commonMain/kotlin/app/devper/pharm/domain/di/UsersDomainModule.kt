package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.CreateUserUseCase
import app.devper.pharm.domain.usecase.DeleteUserUseCase
import app.devper.pharm.domain.usecase.GetUsersUseCase
import app.devper.pharm.domain.usecase.SetUserPasswordUseCase
import app.devper.pharm.domain.usecase.SetUserRoleUseCase
import app.devper.pharm.domain.usecase.SetUserStatusUseCase
import app.devper.pharm.domain.usecase.UpdateUserUseCase
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
