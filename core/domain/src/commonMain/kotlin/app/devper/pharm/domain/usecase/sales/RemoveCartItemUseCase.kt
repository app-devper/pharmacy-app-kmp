package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.repository.sales.CartRepository

class RemoveCartItemUseCase(private val cart: CartRepository) :
    BaseSyncUseCase<CartLineKey, Unit>() {
    override fun execute(param: CartLineKey) = cart.remove(param)
}
