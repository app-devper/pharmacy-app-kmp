package app.devper.pharm.domain.param.sales

import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.common.value.Money

data class RunCheckoutParam(
    val received: Money,
    val allowOversell: Boolean = false,
    val kySkippedByCashier: Boolean = false,
    /** KY forms for this bill (blank sale id); queued with it if it goes offline. */
    val kyForms: List<KyForm> = emptyList(),
)
