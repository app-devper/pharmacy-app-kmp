package app.devper.pharm.presentation.stock.exception

import app.devper.pharm.common.AppException

sealed class StockUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadStockFailed(cause: Throwable? = null) : StockUiStateError("stock.load_failed", cause)
    class LoadHistoryFailed(cause: Throwable? = null) : StockUiStateError("stock.load_history_failed", cause)
}

sealed class DrugFormUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class NotFound : DrugFormUiStateError("stock.drug_not_found")
    class LoadDrugFailed(cause: Throwable? = null) : DrugFormUiStateError("stock.drug_form_load_failed", cause)
    class InvalidInitialLotExpiry : DrugFormUiStateError("stock.drug_form_lot_expiry_invalid")
}

sealed class DrugLotsUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadLotsFailed(cause: Throwable? = null) : DrugLotsUiStateError("stock.load_lots_failed", cause)
    class InvalidExpiry : DrugLotsUiStateError("stock.invalid_expiry")
    class AddLotFailed(cause: Throwable? = null) : DrugLotsUiStateError("stock.add_lot_failed", cause)
    class RemoveLotFailed(cause: Throwable? = null) : DrugLotsUiStateError("stock.remove_lot_failed", cause)
}
