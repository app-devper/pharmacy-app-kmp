package app.devper.pharm.presentation.sell

import app.devper.pharm.presentation.sell.sibling.CheckoutViewModel
import app.devper.pharm.presentation.sell.sibling.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.sibling.DrugPickerViewModel
import app.devper.pharm.presentation.sell.sibling.ParkedCartViewModel
import app.devper.pharm.presentation.sell.sibling.VoidSaleViewModel

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.usecase.ClearCartUseCase
import app.devper.pharm.domain.usecase.RefreshSettingsUseCase
import app.devper.pharm.domain.usecase.RemoveCartItemUseCase
import app.devper.pharm.domain.usecase.SetCartDiscountUseCase
import app.devper.pharm.domain.usecase.SetCartQtyUseCase
import app.devper.pharm.domain.usecase.SetCashReceivedUseCase
import app.devper.pharm.domain.usecase.SetLineDiscountUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SellViewModel(
    cartState: CartStateProvider,
    settings: SettingsProvider,
    private val setCartQty: SetCartQtyUseCase,
    private val removeItem: RemoveCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val setLineDiscount: SetLineDiscountUseCase,
    private val setCartDiscount: SetCartDiscountUseCase,
    private val setCashReceived: SetCashReceivedUseCase,
    private val refreshSettings: RefreshSettingsUseCase,
) : BaseViewModel<SellUiState>(SellUiState()) {

    init {
        cartState.state
            .onEach { snap ->
                setState {
                    copy(
                        cart = snap.items,
                        customer = snap.selectedCustomer,
                        cartDiscount = snap.cartDiscount,
                        activeTier = snap.activeTier,
                        received = snap.cashReceived,
                        receipt = snap.lastReceipt,
                        lineDiscountFor = lineDiscountFor?.let { open ->
                            snap.items.firstOrNull { l -> l.key == open.key }
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
        settings.state
            .onEach { s -> setState { copy(settings = s) } }
            .launchIn(viewModelScope)

        viewModelScope.launch { refreshSettings() }
    }

    fun onReceivedChange(value: String) = setCashReceived(value)

    fun onSetQty(key: CartLineKey, displayQty: Int) = setCartQty(key, displayQty)
    fun onRemove(key: CartLineKey) = removeItem(key)
    fun onClearCart() = clearCart()

    fun onOpenLineDiscount(line: CartLine) = setState { copy(lineDiscountFor = line) }
    fun onCloseLineDiscount() = setState { copy(lineDiscountFor = null) }
    fun onApplyLineDiscount(key: CartLineKey, discount: Double) {
        setLineDiscount(key, discount)
        setState { copy(lineDiscountFor = null) }
    }

    fun onOpenCartDiscount() = setState { copy(cartDiscountSheetOpen = true) }
    fun onCloseCartDiscount() = setState { copy(cartDiscountSheetOpen = false) }
    fun onApplyCartDiscount(discount: CartDiscount) {
        setCartDiscount(discount)
        setState { copy(cartDiscountSheetOpen = false) }
    }

    fun dismissError() = setState { copy(error = null) }
}
