package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseSyncUseCase

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.AddCartItemParam
import app.devper.pharm.domain.repository.CartRepository

class AddToCartUseCase(private val cart: CartRepository) :
    BaseSyncUseCase<AddCartItemParam, Unit>() {
    override fun execute(param: AddCartItemParam) = cart.add(param)
    operator fun invoke(drug: Drug, altUnit: AltUnit? = null): Result<Unit> =
        invoke(AddCartItemParam(drug = drug, altUnit = altUnit))
}
