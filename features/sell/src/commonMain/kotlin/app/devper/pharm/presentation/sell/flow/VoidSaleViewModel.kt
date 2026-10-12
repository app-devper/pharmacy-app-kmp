package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.cart.Cart
import app.devper.pharm.domain.param.sales.VoidSaleParam
import app.devper.pharm.domain.usecase.sales.VoidSaleUseCase
import app.devper.pharm.domain.validation.SaleValidationError
import app.devper.pharm.presentation.sell.exception.VoidSaleUiStateError
import app.devper.pharm.ui.common.BaseViewModel

class VoidSaleViewModel(
    private val voidSale: VoidSaleUseCase,
    private val cart: Cart,
) : BaseViewModel<VoidSaleUiState>(VoidSaleUiState()) {

    fun openSheet() = setState { copy(sheetOpen = true) }
    fun closeSheet() = setState { copy(sheetOpen = false) }

    fun confirm(saleId: String, reason: String) {
        if (saleId.isBlank()) {
            setState { copy(sheetOpen = false, errorState = VoidSaleUiStateError.MissingBillId()) }
            return
        }
        if (reason.isBlank()) {
            setState { copy(sheetOpen = false, errorState = VoidSaleUiStateError.ReasonRequired()) }
            return
        }
        setState { copy(submitting = true, errorState = null) }
        launchResult(
            block = { voidSale(VoidSaleParam(saleId = saleId, reason = reason)) },
            onSuccess = {
                setState { copy(submitting = false, sheetOpen = false) }
                cart.dismissReceipt()
            },
            onFailure = { e ->
                setState {
                    copy(
                        submitting = false,
                        sheetOpen = false,
                        errorState = (e as? SaleValidationError) ?: VoidSaleUiStateError.VoidFailed(e),
                    )
                }
            },
        )
    }

    fun dismissError() = setState { copy(errorState = null) }
}
