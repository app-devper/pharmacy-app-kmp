package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.repository.sales.CartRepository

class ClearCustomerUseCase(private val cart: CartRepository) : BaseSyncUseCase<Unit, Unit>() {
    override fun execute(param: Unit) = cart.clearCustomer()
    operator fun invoke(): Result<Unit> = invoke(Unit)
}
