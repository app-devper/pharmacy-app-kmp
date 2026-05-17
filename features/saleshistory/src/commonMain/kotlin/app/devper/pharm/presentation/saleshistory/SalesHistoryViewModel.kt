package app.devper.pharm.presentation.saleshistory

import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.domain.param.ReturnLineParam
import app.devper.pharm.domain.param.SaleHistoryFilterParam
import app.devper.pharm.domain.param.SubmitReturnParam
import app.devper.pharm.domain.usecase.GetSaleHistoryUseCase
import app.devper.pharm.domain.usecase.GetSaleItemsUseCase
import app.devper.pharm.domain.usecase.SubmitSaleReturnUseCase
import app.devper.pharm.domain.util.SaleReturnQty
import app.devper.pharm.presentation.saleshistory.internal.millisToYmd
import app.devper.pharm.ui.common.BaseViewModel

class SalesHistoryViewModel(
    private val getHistory: GetSaleHistoryUseCase,
    private val getItems: GetSaleItemsUseCase,
    private val submitReturn: SubmitSaleReturnUseCase,
) : BaseViewModel<SalesHistoryUiState>(SalesHistoryUiState()) {

    init { loadList() }

    fun onFromChange(value: String) = setState { copy(from = value) }
    fun onToChange(value: String) = setState { copy(to = value) }
    fun onQueryChange(value: String) = setState { copy(query = value) }

    fun onFromMillisChange(millis: Long?) = onFromChange(millisToYmd(millis))
    fun onToMillisChange(millis: Long?) = onToChange(millisToYmd(millis))

    fun applyFilter() = loadList()

    fun loadList() {
        val s = current
        setState { copy(loading = true, error = null) }
        launchResult(
            block = {
                getHistory(
                    SaleHistoryFilterParam(
                        from = s.from.takeIf { it.isNotBlank() },
                        to = s.to.takeIf { it.isNotBlank() },
                        query = s.query.takeIf { it.isNotBlank() },
                    ),
                )
            },
            onSuccess = { list -> setState { copy(loading = false, sales = list) } },
            onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดรายการบิลไม่สำเร็จ") } },
        )
    }

    fun onSelectSale(sale: SaleSummary) {
        setState { copy(selected = sale, items = emptyList(), itemsLoading = true) }
        launchResult(
            block = { getItems(sale.id) },
            onSuccess = { items -> setState { copy(itemsLoading = false, items = items) } },
            onFailure = { e -> setState { copy(itemsLoading = false, error = e.message ?: "โหลดรายการสินค้าไม่สำเร็จ") } },
        )
    }

    fun onStartReturn(sale: SaleSummary) {
        if (sale.voided) return
        setState { copy(selected = sale, items = emptyList(), itemsLoading = true) }
        launchResult(
            block = { getItems(sale.id) },
            onSuccess = { items ->
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
            onFailure = { e -> setState { copy(itemsLoading = false, error = e.message ?: "โหลดรายการสินค้าไม่สำเร็จ") } },
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
        val baseQty = SaleReturnQty.resolve(item, displayQty)
        setState { copy(returnDraft = returnDraft + (saleItemId to baseQty)) }
    }

    fun confirmReturn() {
        val s = current
        val sale = s.selected ?: return
        val lines = s.returnDraft
            .filter { (_, qty) -> qty > 0 }
            .map { (saleItemId, qty) -> ReturnLineParam(saleItemId, qty) }
        setState { copy(submittingReturn = true, error = null) }
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
            onFailure = { e -> setState { copy(submittingReturn = false, error = e.message ?: "บันทึกการคืนสินค้าไม่สำเร็จ") } },
        )
    }

    fun dismissError() = setState { copy(error = null) }
}

