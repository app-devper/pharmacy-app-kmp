package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.repository.CartRepository

class DiscardParkedCartUseCase(private val cart: CartRepository) : BaseSyncUseCase<Int, Unit>() {
    override fun execute(param: Int) = cart.discardSlot(param)
}
