package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.CartSnapshot
import app.devper.pharm.domain.model.CartState
import app.devper.pharm.domain.repository.sales.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartStateProvider(private val cart: CartRepository) {
    val state: Flow<CartSnapshot> = cart.state.map { snapshotOf(it) }

    val current: CartSnapshot get() = snapshotOf(cart.state.value)

    private fun snapshotOf(state: CartState): CartSnapshot {
        val active = state.active
        return CartSnapshot(
            items = active.items,
            selectedCustomer = active.customer,
            cartDiscount = active.cartDiscount,
            activeTier = active.activeTier,
            cashReceived = active.cashReceived,
            lastReceipt = state.lastReceipt,
        )
    }
}
