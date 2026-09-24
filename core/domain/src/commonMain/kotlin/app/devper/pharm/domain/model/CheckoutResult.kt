package app.devper.pharm.domain.model

import app.devper.pharm.common.AppException

sealed interface CheckoutOutcome {
    data object CartChanged : CheckoutOutcome
    data object OfflineSaved : CheckoutOutcome
    data class Success(val sale: Sale) : CheckoutOutcome
    data class NeedsOversellConfirm(val shortfalls: List<OversellShortfall>) : CheckoutOutcome
}

class CheckoutFailure(
    cause: Throwable,
) : AppException(cause.message ?: "checkout_failed", cause)
