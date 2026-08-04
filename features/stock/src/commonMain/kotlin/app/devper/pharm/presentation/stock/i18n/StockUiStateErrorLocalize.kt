package app.devper.pharm.presentation.stock.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.stock.exception.DrugFormUiStateError
import app.devper.pharm.presentation.stock.exception.DrugLotsUiStateError
import app.devper.pharm.presentation.stock.exception.StockUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeStock(s: PharmStrings): String = when (this) {
    is StockUiStateError.LoadStockFailed -> s.stockLoadFailed
    is StockUiStateError.LoadHistoryFailed -> s.stockLoadHistoryFailed
    is DrugFormUiStateError.NotFound -> s.stockDrugFormNotFound
    is DrugFormUiStateError.LoadDrugFailed -> s.stockDrugFormLoadFailed
    is DrugFormUiStateError.InvalidInitialLotExpiry -> s.stockDrugFormLotExpiryInvalid
    is DrugLotsUiStateError.LoadLotsFailed -> s.stockLoadLotsFailed
    is DrugLotsUiStateError.InvalidExpiry -> s.stockInvalidExpiry
    is DrugLotsUiStateError.AddLotFailed -> s.stockAddLotFailed
    is DrugLotsUiStateError.RemoveLotFailed -> s.stockRemoveLotFailed
    else -> localizeCommon(s)
}
