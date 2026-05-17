package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.repository.CartRepository

class RemoveCartItemUseCase(private val cart: CartRepository) :
    BaseSyncUseCase<CartLineKey, Unit>() {
    override fun execute(param: CartLineKey) = cart.remove(param)
}
