package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.repository.sales.CartRepository

class SetCartDiscountUseCase(private val cart: CartRepository) : BaseSyncUseCase<CartDiscount, Unit>() {
    override fun execute(param: CartDiscount) = cart.setCartDiscount(param)
}
