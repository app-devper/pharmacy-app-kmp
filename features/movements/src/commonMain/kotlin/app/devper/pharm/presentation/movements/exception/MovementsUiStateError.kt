package app.devper.pharm.presentation.movements.exception

import app.devper.pharm.common.AppException

sealed class MovementsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadHistoryFailed(cause: Throwable? = null) : MovementsUiStateError("movements.load_history_failed", cause)
}
