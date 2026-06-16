package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.sales.ReturnLineParam
import app.devper.pharm.domain.param.sales.SaleHistoryFilterParam
import app.devper.pharm.domain.param.sales.SubmitReturnParam
import app.devper.pharm.domain.observer.TimeZoneProvider
import app.devper.pharm.domain.usecase.sales.GetSaleHistoryUseCase
import app.devper.pharm.domain.usecase.sales.GetSaleItemsUseCase
import app.devper.pharm.domain.usecase.sales.SubmitSaleReturnUseCase
import app.devper.pharm.domain.extension.resolveReturnQty
import app.devper.pharm.domain.validation.SaleValidationError
import app.devper.pharm.presentation.saleshistory.exception.SalesHistoryUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel
import app.devper.pharm.ui.format.DateRangeFilter

class SalesHistoryViewModel(
    private val getHistory: GetSaleHistoryUseCase,
    private val getItems: GetSaleItemsUseCase,
    private val submitReturn: SubmitSaleReturnUseCase,
    timeZoneProvider: TimeZoneProvider,
) : BaseLoadableViewModel<SalesHistoryUiState>(
    SalesHistoryUiState(dateRange = DateRangeFilter(tz = timeZoneProvider.current)),
) {

    init { loadList() }

    fun onFromChange(value: String) = setState { copy(dateRange = dateRange.withFrom(value)) }
    fun onToChange(value: String) = setState { copy(dateRange = dateRange.withTo(value)) }
    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun onFromMillisChange(millis: Long?) = setState { copy(dateRange = dateRange.withFromMillis(millis)) }
    fun onToMillisChange(millis: Long?) = setState { copy(dateRange = dateRange.withToMillis(millis)) }

    fun applyFilter() = loadList()

    fun onSelectRange(from: String, to: String) {
        setState { copy(dateRange = dateRange.withFrom(from).withTo(to)) }
        loadList()
    }

    fun loadList() {
        val s = current
        setState { copy(loading = true, errorState = null) }
        launchResult(
            block = {
                getHistory(
                    SaleHistoryFilterParam(
                        from = s.dateRange.fromDate,
                        to = s.dateRange.toDate,
                        query = s.query.takeIf { it.isNotBlank() },
                    ),
                )
            },
            onSuccess = { list -> setState { copy(loading = false, sales = list) } },
            onFailure = { e -> setState { copy(loading = false, errorState = SalesHistoryUiStateError.LoadBillsFailed(e)) } },
        )
    }

    fun onSelectSale(sale: SaleSummary) {
        val targetId = sale.id
        setState { copy(selected = sale, items = emptyList(), itemsLoading = true) }
        launchResult(
            block = { getItems(targetId) },
            onSuccess = { items ->
                if (current.selected?.id != targetId) return@launchResult
                setState { copy(itemsLoading = false, items = items) }
            },
            onFailure = { e ->
                if (current.selected?.id != targetId) return@launchResult
                setState { copy(itemsLoading = false, errorState = SalesHistoryUiStateError.LoadItemsFailed(e)) }
            },
        )
    }

    fun onViewBill(sale: SaleSummary) {
        setState { copy(billSheetOpen = true) }
        onSelectSale(sale)
    }

    fun onCloseBill() = setState { copy(billSheetOpen = false) }

    fun onStartReturn(sale: SaleSummary) {
        if (sale.voided) return
        val targetId = sale.id
        setState { copy(selected = sale, items = emptyList(), itemsLoading = true) }
        launchResult(
            block = { getItems(targetId) },
            onSuccess = { items ->
                if (current.selected?.id != targetId) return@launchResult
                val zero = items.associate { it.id to 0 }
                setState {
                    copy(
                        itemsLoading = false,
                        items = items,
                        returnSheetOpen = true,
                        returnDraft = zero,
                        returnReason = "",
                    )
                }
            },
            onFailure = { e ->
                if (current.selected?.id != targetId) return@launchResult
                setState { copy(itemsLoading = false, errorState = SalesHistoryUiStateError.LoadItemsFailed(e)) }
            },
        )
    }

    fun onClearSelection() = setState {
        copy(selected = null, items = emptyList(), returnSheetOpen = false, returnDraft = emptyMap())
    }

    fun onOpenReturnSheet() {
        val zero = current.items.associate { it.id to 0 }
        setState { copy(returnSheetOpen = true, returnDraft = zero, returnReason = "") }
    }

    fun onCloseReturnSheet() = setState {
        copy(returnSheetOpen = false, returnDraft = emptyMap(), returnReason = "")
    }

    fun onReturnReasonChange(value: String) = setState { copy(returnReason = value) }

    fun onReturnLineQtyChange(saleItemId: String, displayQty: Int) {
        val item = current.items.firstOrNull { it.id == saleItemId } ?: return
        val baseQty = item.resolveReturnQty(displayQty)
        setState { copy(returnDraft = returnDraft + (saleItemId to baseQty)) }
    }

    fun confirmReturn() {
        val s = current
        val sale = s.selected ?: return
        val lines = s.returnDraft
            .filter { (_, qty) -> qty > 0 }
            .map { (saleItemId, qty) -> ReturnLineParam(saleItemId, qty) }
        setState { copy(submittingReturn = true, errorState = null) }
        launchResult(
            block = { submitReturn(SubmitReturnParam(saleId = sale.id, reason = s.returnReason, items = lines)) },
            onSuccess = {
                setState {
                    copy(
                        submittingReturn = false,
                        returnSheetOpen = false,
                        returnDraft = emptyMap(),
                        returnReason = "",
                    )
                }
                onSelectSale(sale)
            },
            onFailure = { e -> setState { copy(submittingReturn = false, errorState = (e as? SaleValidationError) ?: SalesHistoryUiStateError.SubmitReturnFailed(e)) } },
        )
    }
}

