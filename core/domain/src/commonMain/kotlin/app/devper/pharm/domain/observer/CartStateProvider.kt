package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartSnapshot
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CartStateProvider(private val cart: CartRepository) {
    val state: Flow<CartSnapshot> = combine(
        cart.items,
        cart.selectedCustomer,
        cart.cartDiscount,
        cart.activeTier,
        cart.cashReceived,
        cart.lastReceipt,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        CartSnapshot(
            items = values[0] as List<CartLine>,
            selectedCustomer = values[1] as Customer?,
            cartDiscount = values[2] as CartDiscount,
            activeTier = values[3] as String,
            cashReceived = values[4] as String,
            lastReceipt = values[5] as Sale?,
        )
    }
}
