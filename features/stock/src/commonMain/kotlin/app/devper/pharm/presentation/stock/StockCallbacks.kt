package app.devper.pharm.presentation.stock

import app.devper.pharm.domain.model.Drug

data class StockCallbacks(
    val onQueryChange: (String) -> Unit = {},
    val onTypeFilterChange: (StockTypeFilter) -> Unit = {},
    val onAddDrug: () -> Unit = {},
    val onEditDrug: (Drug) -> Unit = {},
    val onOpenLots: (Drug) -> Unit = {},
    val onOpenAdjust: (Drug) -> Unit = {},
    val onOpenHistory: (Drug) -> Unit = {},
    val onExportExcel: () -> Unit = {},
    val onImport: () -> Unit = {},
    val onOpenReorderSuggestions: () -> Unit = {},
    val onOpenExpiry: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)

enum class StockTypeFilter {
    All,
    Current,
    Herb,
    Supplement;

    fun matches(rawType: String?): Boolean {
        if (this == All) return true
        val type = rawType?.trim()?.lowercase().orEmpty()
        return when (this) {
            All        -> true
            Herb       -> type.contains("herb") || type.contains("สมุนไพร")
            Supplement -> type.contains("supp") || type.contains("อาหารเสริม")
            Current    -> !(type.contains("herb") || type.contains("สมุนไพร") ||
                            type.contains("supp") || type.contains("อาหารเสริม"))
        }
    }
}
