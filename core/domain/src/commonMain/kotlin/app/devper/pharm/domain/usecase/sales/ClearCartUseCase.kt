package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.repository.CartRepository

class ClearCartUseCase(private val cart: CartRepository) : BaseSyncUseCase<Unit, Unit>() {
    override fun execute(param: Unit) = cart.clear()
    operator fun invoke(): Result<Unit> = invoke(Unit)
}
