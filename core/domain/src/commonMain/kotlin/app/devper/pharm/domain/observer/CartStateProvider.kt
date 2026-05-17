package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.CartSnapshot
import app.devper.pharm.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CartStateProvider(private val cart: CartRepository) {
    val state: Flow<CartSnapshot> = combine(cart.active, cart.lastReceipt) { active, lastReceipt ->
        CartSnapshot(
            items = active.items,
            selectedCustomer = active.customer,
            cartDiscount = active.cartDiscount,
            activeTier = active.activeTier,
            cashReceived = active.cashReceived,
            lastReceipt = lastReceipt,
        )
    }
}
