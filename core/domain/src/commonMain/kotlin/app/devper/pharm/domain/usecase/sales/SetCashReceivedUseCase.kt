package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.repository.CartRepository

class SetCashReceivedUseCase(private val cart: CartRepository) : BaseSyncUseCase<String, Unit>() {
    override fun execute(param: String) = cart.setCashReceived(param)
}
