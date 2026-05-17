package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.repository.CartRepository

class ClearCustomerUseCase(private val cart: CartRepository) : BaseSyncUseCase<Unit, Unit>() {
    override fun execute(param: Unit) = cart.clearCustomer()
    operator fun invoke(): Result<Unit> = invoke(Unit)
}
