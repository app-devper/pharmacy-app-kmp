package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.CartSnapshot
import app.devper.pharm.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartStateProvider(private val cart: CartRepository) {
    val state: Flow<CartSnapshot> = cart.state.map { current ->
        val active = current.active
        CartSnapshot(
            items = active.items,
            selectedCustomer = active.customer,
            cartDiscount = active.cartDiscount,
            activeTier = active.activeTier,
            cashReceived = active.cashReceived,
            lastReceipt = current.lastReceipt,
        )
    }
}
