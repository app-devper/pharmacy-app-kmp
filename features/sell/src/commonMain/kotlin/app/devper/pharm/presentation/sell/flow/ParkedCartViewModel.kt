package app.devper.pharm.presentation.sell.flow

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.ParkedCartsProvider
import app.devper.pharm.domain.usecase.sales.DiscardParkedCartUseCase
import app.devper.pharm.domain.usecase.sales.ParkCartUseCase
import app.devper.pharm.domain.usecase.sales.RestoreCartUseCase
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ParkedCartViewModel(
    parkedCarts: ParkedCartsProvider,
    cartState: CartStateProvider,
    private val parkCart: ParkCartUseCase,
    private val restoreCart: RestoreCartUseCase,
    private val discardParked: DiscardParkedCartUseCase,
) : BaseViewModel<ParkedCartUiState>(ParkedCartUiState()) {

    init {
        parkedCarts.slots
            .onEach { slots -> setState { copy(parkedSlots = slots) } }
            .launchIn(viewModelScope)

        cartState.state
            .onEach { snap -> setState { copy(activeCartIsEmpty = snap.items.isEmpty()) } }
            .launchIn(viewModelScope)
    }

    fun openSheet() = setState { copy(sheetOpen = true) }
    fun closeSheet() = setState { copy(sheetOpen = false) }

    fun tapSlot(slot: Int) {
        val s = current
        if (slot == s.activeSlot) {
            setState { copy(sheetOpen = false) }
            return
        }
        parkCart(s.activeSlot)
        restoreCart(slot)
        setState { copy(activeSlot = slot, sheetOpen = false) }
    }

    fun newBillOnNextTab() {
        val s = current
        val target = s.parkedSlots.indices.firstOrNull { it != s.activeSlot && s.parkedSlots[it] == null }
        if (target == null) {
            setState { copy(sheetOpen = true) }
            return
        }
        tapSlot(target)
    }

    fun requestOverwrite(slot: Int) {
        if (current.activeCartIsEmpty) return
        setState { copy(overwriteSlot = slot) }
    }

    fun cancelOverwrite() = setState { copy(overwriteSlot = null) }

    fun confirmOverwrite() {
        val slot = current.overwriteSlot ?: return
        parkCart(slot)
        setState { copy(overwriteSlot = null, sheetOpen = false) }
    }

    fun discard(slot: Int) = discardParked(slot)
}
