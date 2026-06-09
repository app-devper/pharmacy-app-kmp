package app.devper.pharm.presentation.planning.exception

import app.devper.pharm.common.AppException

sealed class LowStockUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class TrackStockFailed(cause: Throwable? = null) : LowStockUiStateError("planning.track_stock_failed", cause)
}
