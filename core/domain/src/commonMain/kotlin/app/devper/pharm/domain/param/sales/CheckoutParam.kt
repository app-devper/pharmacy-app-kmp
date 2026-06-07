package app.devper.pharm.domain.param

import app.devper.pharm.common.value.Money

data class CheckoutParam(
    val items: List<CheckoutLineParam>,
    val received: Money,
    val customerId: String? = null,
    val discount: Money = Money.Zero,
    val priceTier: String = "",

    val clientRequestId: String? = null,
    val kySkippedByCashier: Boolean = false,
)

data class CheckoutLineParam(
    val drugId: String,

    val qty: Int,

    val unitPrice: Money,

    val originalUnitPrice: Money,

    val itemDiscount: Money = Money.Zero,
    val priceTier: String = "",

    val allowOversell: Boolean = false,

    val unit: String = "",

    val unitFactor: Int = 0,
)
