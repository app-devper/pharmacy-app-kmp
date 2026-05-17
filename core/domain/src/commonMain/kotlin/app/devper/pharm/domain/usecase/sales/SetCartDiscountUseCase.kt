package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.repository.CartRepository

class SetCartDiscountUseCase(private val cart: CartRepository) : BaseSyncUseCase<CartDiscount, Unit>() {
    override fun execute(param: CartDiscount) = cart.setCartDiscount(param)
}
