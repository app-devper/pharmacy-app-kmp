package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.param.SetCartQtyParam
import app.devper.pharm.domain.repository.CartRepository

class SetCartQtyUseCase(private val cart: CartRepository) : BaseSyncUseCase<SetCartQtyParam, Unit>() {
    override fun execute(param: SetCartQtyParam) = cart.setQty(param)

    operator fun invoke(key: CartLineKey, displayQty: Int): Result<Unit> =
        invoke(SetCartQtyParam(key = key, displayQty = displayQty))
}
