package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.repository.sales.CartRepository

class DiscardParkedCartUseCase(private val cart: CartRepository) : BaseSyncUseCase<Int, Unit>() {
    override fun execute(param: Int) = cart.discardSlot(param)
}
