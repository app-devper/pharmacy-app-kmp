package app.devper.pharm.domain.model

import app.devper.pharm.common.AppException

sealed interface CheckoutOutcome {
    data class Success(val sale: Sale) : CheckoutOutcome
    data class NeedsOversellConfirm(val shortfalls: List<OversellShortfall>) : CheckoutOutcome
}

class CheckoutFailure(
    cause: Throwable,
    val serializedRequest: String? = null,
    val clientRequestId: String? = null,
) : AppException(cause.message ?: "checkout_failed", cause)
