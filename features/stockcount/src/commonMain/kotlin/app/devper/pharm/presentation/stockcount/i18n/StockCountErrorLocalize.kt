package app.devper.pharm.presentation.stockcount.i18n

import app.devper.pharm.common.AppException
import app.devper.pharm.presentation.stockcount.exception.StockCountUiStateError
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.localizeCommon

fun AppException.localizeStockCount(s: PharmStrings): String = when (this) {
    is StockCountUiStateError.LoadRoundsFailed -> s.stockCountLoadRoundsFailed
    is StockCountUiStateError.LoadDrugsFailed -> s.stockCountLoadDrugsFailed
    else -> localizeCommon(s)
}
