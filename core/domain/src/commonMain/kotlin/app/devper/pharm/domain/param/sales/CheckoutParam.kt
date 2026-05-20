package app.devper.pharm.domain.param

data class CheckoutParam(
    val items: List<CheckoutLineParam>,
    val received: Double,
    val customerId: String? = null,
    val discount: Double = 0.0,
    val priceTier: String = "",

    val clientRequestId: String? = null,
    val kySkippedByCashier: Boolean = false,
)

data class CheckoutLineParam(
    val drugId: String,

    val qty: Int,

    val unitPrice: Double,

    val originalUnitPrice: Double,

    val itemDiscount: Double = 0.0,
    val priceTier: String = "",

    val allowOversell: Boolean = false,

    val unit: String = "",

    val unitFactor: Int = 0,
)
