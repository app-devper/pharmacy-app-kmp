package app.devper.pharm.presentation.stockcount.exception

import app.devper.pharm.common.AppException

sealed class StockCountUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadRoundsFailed(cause: Throwable? = null) : StockCountUiStateError("stockcount.load_rounds_failed", cause)
    class LoadDrugsFailed(cause: Throwable? = null) : StockCountUiStateError("stockcount.load_drugs_failed", cause)
}
