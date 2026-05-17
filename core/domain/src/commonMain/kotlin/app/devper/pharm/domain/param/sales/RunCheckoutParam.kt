package app.devper.pharm.domain.param

data class RunCheckoutParam(
    val received: Double,
    val allowOversell: Boolean = false,
    val clientRequestId: String? = null,
)
