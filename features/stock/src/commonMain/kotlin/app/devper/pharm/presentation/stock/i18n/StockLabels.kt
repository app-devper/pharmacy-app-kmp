package app.devper.pharm.presentation.stock.i18n

import app.devper.pharm.presentation.stock.StockTypeFilter
import app.devper.pharm.ui.i18n.PharmStrings

fun StockTypeFilter.label(s: PharmStrings): String = when (this) {
    StockTypeFilter.All -> s.stockTypeFilterAll
    StockTypeFilter.Current -> s.stockTypeFilterCurrent
    StockTypeFilter.Herb -> s.stockTypeFilterHerb
    StockTypeFilter.Supplement -> s.stockTypeFilterSupplement
}
