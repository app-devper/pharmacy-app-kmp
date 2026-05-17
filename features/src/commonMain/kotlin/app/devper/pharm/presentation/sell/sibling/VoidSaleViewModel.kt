package app.devper.pharm.presentation.sell.sibling

import app.devper.pharm.domain.param.VoidSaleParam
import app.devper.pharm.domain.usecase.DismissReceiptUseCase
import app.devper.pharm.domain.usecase.VoidSaleUseCase
import app.devper.pharm.ui.common.BaseViewModel

class VoidSaleViewModel(
    private val voidSale: VoidSaleUseCase,
    private val dismissReceiptUseCase: DismissReceiptUseCase,
) : BaseViewModel<VoidSaleUiState>(VoidSaleUiState()) {

    fun openSheet() = setState { copy(sheetOpen = true) }
    fun closeSheet() = setState { copy(sheetOpen = false) }

    fun confirm(saleId: String, reason: String) {
        if (saleId.isBlank()) {
            setState { copy(sheetOpen = false, error = "ไม่สามารถยกเลิกบิลนี้: ไม่พบรหัสบิล") }
            return
        }
        setState { copy(submitting = true, error = null) }
        launchResult(
            block = { voidSale(VoidSaleParam(saleId = saleId, reason = reason)) },
            onSuccess = {
                setState { copy(submitting = false, sheetOpen = false) }
                dismissReceiptUseCase()
            },
            onFailure = { e ->
                setState {
                    copy(
                        submitting = false,
                        sheetOpen = false,
                        error = e.message ?: "ยกเลิกบิลไม่สำเร็จ",
                    )
                }
            },
        )
    }

    fun dismissError() = setState { copy(error = null) }
}
