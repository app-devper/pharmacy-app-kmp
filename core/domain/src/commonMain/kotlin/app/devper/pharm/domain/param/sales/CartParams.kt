package app.devper.pharm.domain.param.sales

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartLineKey
import app.devper.pharm.domain.model.Drug

data class AddCartItemParam(
    val drug: Drug,
    val altUnit: AltUnit? = null,
)

data class SetCartQtyParam(
    val key: CartLineKey,
    val displayQty: Int,
)

data class SetLineDiscountParam(
    val key: CartLineKey,
    val discount: Double,
)
