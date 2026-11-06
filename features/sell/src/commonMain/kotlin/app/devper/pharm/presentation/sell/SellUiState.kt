package app.devper.pharm.presentation.sell

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.PricedBill
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.priceBill
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.BaseUiState

data class SellUiState(
    val cart: List<CartLine> = emptyList(),
    val customer: Customer? = null,
    val cartDiscount: CartDiscount = CartDiscount.None,
    val activeTier: String = Tier.Retail,
    val received: String = "",
    val receipt: Sale? = null,
    val cartDiscountSheetOpen: Boolean = false,
    val lineDiscountFor: CartLine? = null,
    val showClearConfirm: Boolean = false,

    val settings: Settings = Settings(),
    override val loading: Boolean = false,
    val errorState: AppException? = null,
) : BaseUiState {

    override val domainError: AppException? get() = errorState

    val bill: PricedBill get() = priceBill(cart, cartDiscount)
    val subtotal: Money get() = bill.subtotal
    val cartDiscountAmount: Money get() = bill.cartDiscountAmount
    val total: Money get() = bill.total
    val grossSubtotal: Money get() = bill.grossSubtotal
    val itemDiscountTotal: Money get() = bill.itemDiscountTotal
    val receivedNum: Double get() = received.toDoubleOrNull() ?: 0.0
    val change: Money get() = bill.change(Money(receivedNum))
    val shortfall: Money get() = bill.shortfall(Money(receivedNum))
    val cartItemCount: Int get() = cart.sumOf { it.qty }

    val kyInitialFields: KyCaptureFields
        get() {
            val name = customer?.name.orEmpty()
            return KyCaptureFields(
                ky10BuyerName = name,
                ky10BuyerAddress = settings.ky.defaultBuyerAddress,
                ky11BuyerName = name,
                ky11Pharmacist = settings.pharmacist.name,
                ky12PatientName = name,
            )
        }
}
