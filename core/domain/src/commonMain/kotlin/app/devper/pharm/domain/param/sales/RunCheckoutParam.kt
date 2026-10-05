package app.devper.pharm.domain.param.sales

import app.devper.pharm.domain.model.SaleKyCapture
import app.devper.pharm.common.value.Money

data class RunCheckoutParam(
    val received: Money,
    val allowOversell: Boolean = false,
    val kySkippedByCashier: Boolean = false,
    /** The bill's KY capture; it travels with the sale, queued or not. */
    val ky: SaleKyCapture? = null,
)
