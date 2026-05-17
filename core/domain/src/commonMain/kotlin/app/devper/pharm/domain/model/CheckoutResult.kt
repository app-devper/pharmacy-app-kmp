package app.devper.pharm.domain.model

sealed interface CheckoutOutcome {
    data class Success(val sale: Sale) : CheckoutOutcome
    data class NeedsOversellConfirm(val shortfalls: List<OversellShortfall>) : CheckoutOutcome
}

class CheckoutFailure(
    cause: Throwable,
    val serializedRequest: String? = null,
    val clientRequestId: String? = null,
) : Exception(cause.message, cause)
