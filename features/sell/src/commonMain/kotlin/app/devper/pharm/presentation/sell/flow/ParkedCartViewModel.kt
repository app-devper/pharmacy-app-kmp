package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.cart.Cart
import androidx.lifecycle.viewModelScope
import app.devper.pharm.ui.common.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ParkedCartViewModel(
    private val cart: Cart,
) : BaseViewModel<ParkedCartUiState>(ParkedCartUiState()) {

    init {
        cart.parkedSlots
            .onEach { slots -> setState { copy(parkedSlots = slots) } }
            .launchIn(viewModelScope)

        cart.snapshots
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
        cart.park(s.activeSlot)
        cart.restore(slot)
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
        cart.park(slot)
        setState { copy(overwriteSlot = null, sheetOpen = false) }
    }

    fun discard(slot: Int) = cart.discard(slot)
}
