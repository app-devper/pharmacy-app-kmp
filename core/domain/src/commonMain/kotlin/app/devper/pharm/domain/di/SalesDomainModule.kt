package app.devper.pharm.domain.di

import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.ParkedCartsProvider
import app.devper.pharm.domain.usecase.AddToCartUseCase
import app.devper.pharm.domain.usecase.CheckoutUseCase
import app.devper.pharm.domain.usecase.ClearCartUseCase
import app.devper.pharm.domain.usecase.ClearCustomerUseCase
import app.devper.pharm.domain.usecase.DiscardParkedCartUseCase
import app.devper.pharm.domain.usecase.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.GetSaleHistoryUseCase
import app.devper.pharm.domain.usecase.GetSaleItemsUseCase
import app.devper.pharm.domain.usecase.GetSaleSummaryUseCase
import app.devper.pharm.domain.usecase.ParkCartUseCase
import app.devper.pharm.domain.usecase.RemoveCartItemUseCase
import app.devper.pharm.domain.usecase.RestoreCartUseCase
import app.devper.pharm.domain.usecase.SelectCustomerUseCase
import app.devper.pharm.domain.usecase.SetCartDiscountUseCase
import app.devper.pharm.domain.usecase.SetCartQtyUseCase
import app.devper.pharm.domain.usecase.SetCashReceivedUseCase
import app.devper.pharm.domain.usecase.SetLineDiscountUseCase
import app.devper.pharm.domain.usecase.SubmitSaleReturnUseCase
import app.devper.pharm.domain.usecase.VoidSaleUseCase
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
    factoryOf(::GetSaleSummaryUseCase)
    factoryOf(::SubmitSaleReturnUseCase)
}
