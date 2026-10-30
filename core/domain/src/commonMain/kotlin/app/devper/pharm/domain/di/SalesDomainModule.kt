package app.devper.pharm.domain.di

import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.cart.Cart
import app.devper.pharm.domain.command.CommandAttempts
import app.devper.pharm.domain.usecase.sales.CheckoutUseCase
import app.devper.pharm.domain.usecase.sales.SubmitSaleReturnUseCase
import app.devper.pharm.domain.usecase.sales.VoidSaleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val salesDomainModule = module {
    single { StockChangeBus() }

    single { Cart(get()) }
    single { CommandAttempts() }
    factory { CheckoutUseCase(get(), get(), get(), get(), get()) }
    factoryOf(::VoidSaleUseCase)
    factory { SubmitSaleReturnUseCase(get(), get(), get()) }
}
