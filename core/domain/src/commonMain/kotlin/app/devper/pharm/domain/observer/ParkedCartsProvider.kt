package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.repository.CartRepository
import kotlinx.coroutines.flow.StateFlow

class ParkedCartsProvider(private val cart: CartRepository) {
    val slots: StateFlow<List<ParkedCart?>> get() = cart.parkedSlots
}
