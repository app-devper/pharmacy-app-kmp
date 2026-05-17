package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.param.SetLineDiscountParam
import app.devper.pharm.domain.repository.CartRepository

class SetLineDiscountUseCase(private val cart: CartRepository) :
    BaseSyncUseCase<SetLineDiscountParam, Unit>() {
    override fun execute(param: SetLineDiscountParam) = cart.setLineDiscount(param)

    operator fun invoke(key: CartLineKey, discount: Double): Result<Unit> =
        invoke(SetLineDiscountParam(key = key, discount = discount))
}
