package app.devper.pharm.domain.param.sales

import app.devper.pharm.common.value.Money

data class RunCheckoutParam(
    val received: Money,
    val allowOversell: Boolean = false,
    val clientRequestId: String? = null,
    val kySkippedByCashier: Boolean = false,
)
