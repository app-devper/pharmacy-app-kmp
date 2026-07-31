package app.devper.pharm.presentation.planning.exception

import app.devper.pharm.common.AppException

sealed class LowStockUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadFailed(cause: Throwable? = null) : LowStockUiStateError("planning.load_low_stock_failed", cause)
    class TrackStockFailed(cause: Throwable? = null) : LowStockUiStateError("planning.track_stock_failed", cause)
}

sealed class ReorderSuggestionsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadFailed(cause: Throwable? = null) : ReorderSuggestionsUiStateError("planning.load_reorder_failed", cause)
}
