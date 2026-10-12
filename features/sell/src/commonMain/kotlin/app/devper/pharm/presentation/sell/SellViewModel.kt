package app.devper.pharm.presentation.sell

import app.devper.pharm.domain.cart.Cart
import app.devper.pharm.presentation.sell.flow.CheckoutViewModel
import app.devper.pharm.presentation.sell.flow.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.flow.DrugPickerViewModel
import app.devper.pharm.presentation.sell.flow.ParkedCartViewModel
import app.devper.pharm.presentation.sell.flow.VoidSaleViewModel

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.observer.SettingsProvider
import app.devper.pharm.domain.usecase.settings.RefreshSettingsUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SellViewModel(
    private val cart: Cart,
    settings: SettingsProvider,
    private val refreshSettings: RefreshSettingsUseCase,
) : BaseViewModel<SellUiState>(SellUiState()) {

    init {
        cart.snapshots
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

        launchResult(
            block = { refreshSettings() },
            onSuccess = { },
            onFailure = { },
        )
    }

    fun onReceivedChange(value: String) = cart.setCashReceived(value)

    fun onSetQty(key: CartLineKey, displayQty: Int) = cart.setQty(key, displayQty)
    fun onRemove(key: CartLineKey) = cart.remove(key)

    fun requestClearCart() {
        if (current.cart.isEmpty()) return
        setState { copy(showClearConfirm = true) }
    }

    fun cancelClearCart() = setState { copy(showClearConfirm = false) }

    fun confirmClearCart() {
        cart.clear()
        setState { copy(showClearConfirm = false) }
    }

    fun onOpenLineDiscount(line: CartLine) = setState { copy(lineDiscountFor = line) }
    fun onCloseLineDiscount() = setState { copy(lineDiscountFor = null) }
    fun onApplyLineDiscount(key: CartLineKey, discount: Double) {
        cart.setLineDiscount(key, discount)
        setState { copy(lineDiscountFor = null) }
    }

    fun onOpenCartDiscount() = setState { copy(cartDiscountSheetOpen = true) }
    fun onCloseCartDiscount() = setState { copy(cartDiscountSheetOpen = false) }
    fun onApplyCartDiscount(discount: CartDiscount) {
        cart.setCartDiscount(discount)
        setState { copy(cartDiscountSheetOpen = false) }
    }

    fun dismissError() = setState { copy(errorState = null) }
}
