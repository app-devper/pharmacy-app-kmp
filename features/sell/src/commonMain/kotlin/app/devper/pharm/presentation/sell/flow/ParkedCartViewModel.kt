package app.devper.pharm.presentation.sell.flow

import androidx.lifecycle.viewModelScope
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.observer.ParkedCartsProvider
import app.devper.pharm.domain.usecase.DiscardParkedCartUseCase
import app.devper.pharm.domain.usecase.ParkCartUseCase
import app.devper.pharm.domain.usecase.RestoreCartUseCase
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
        val slotContent = s.parkedSlots.getOrNull(slot)
        if (slotContent != null) {
            if (!s.activeCartIsEmpty) {
                setState { copy(swapSlot = slot) }
                return
            }
            restoreCart(slot)
            setState { copy(sheetOpen = false) }
            return
        }
        if (s.activeCartIsEmpty) return
        parkCart(slot)
        setState { copy(sheetOpen = false) }
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

    fun cancelSwap() = setState { copy(swapSlot = null) }

    fun confirmSwap() {
        val slot = current.swapSlot ?: return
        restoreCart(slot)
        setState { copy(swapSlot = null, sheetOpen = false) }
    }

    fun discard(slot: Int) = discardParked(slot)
}
