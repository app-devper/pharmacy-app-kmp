package app.devper.pharm.domain.di

import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.ParkedCartsProvider
import app.devper.pharm.domain.usecase.sales.AddToCartUseCase
import app.devper.pharm.domain.usecase.sales.CheckoutUseCase
import app.devper.pharm.domain.usecase.sales.ClearCartUseCase
import app.devper.pharm.domain.usecase.sales.ClearCustomerUseCase
import app.devper.pharm.domain.usecase.sales.DiscardParkedCartUseCase
import app.devper.pharm.domain.usecase.sales.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.sales.GetSaleHistoryUseCase
import app.devper.pharm.domain.usecase.sales.GetSaleItemsUseCase
import app.devper.pharm.domain.usecase.sales.ParkCartUseCase
import app.devper.pharm.domain.usecase.sales.RemoveCartItemUseCase
import app.devper.pharm.domain.usecase.sales.RestoreCartUseCase
import app.devper.pharm.domain.usecase.sales.SelectCustomerUseCase
import app.devper.pharm.domain.usecase.sales.SetCartDiscountUseCase
import app.devper.pharm.domain.usecase.sales.SetCartQtyUseCase
import app.devper.pharm.domain.usecase.sales.SetCashReceivedUseCase
import app.devper.pharm.domain.usecase.sales.SetLineDiscountUseCase
import app.devper.pharm.domain.usecase.sales.SubmitSaleReturnUseCase
import app.devper.pharm.domain.usecase.sales.VoidSaleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val salesDomainModule = module {
    single { StockChangeBus() }

    singleOf(::CartStateProvider)
    singleOf(::ParkedCartsProvider)

    factoryOf(::AddToCartUseCase)
    factoryOf(::SetCartQtyUseCase)
    factoryOf(::RemoveCartItemUseCase)
    factoryOf(::ClearCartUseCase)
    factoryOf(::SelectCustomerUseCase)
    factoryOf(::ClearCustomerUseCase)
    factoryOf(::SetLineDiscountUseCase)
    factoryOf(::SetCartDiscountUseCase)
    factoryOf(::SetCashReceivedUseCase)
    factoryOf(::DismissReceiptUseCase)
    factoryOf(::ParkCartUseCase)
    factoryOf(::RestoreCartUseCase)
    factoryOf(::DiscardParkedCartUseCase)
    factoryOf(::CheckoutUseCase)
    factoryOf(::VoidSaleUseCase)
    factoryOf(::GetSaleHistoryUseCase)
    factoryOf(::GetSaleItemsUseCase)
    factoryOf(::SubmitSaleReturnUseCase)
}
