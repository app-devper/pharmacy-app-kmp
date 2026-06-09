package app.devper.pharm.presentation.sell

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.model.Settings
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

    val subtotal: Money get() = cart.fold(Money.Zero) { acc, line -> acc + line.lineTotal }
    val cartDiscountAmount: Money get() = cartDiscount.apply(subtotal)
    val total: Money get() = (subtotal - cartDiscountAmount).coerceAtLeast(Money.Zero)
    val grossSubtotal: Money get() = cart.fold(Money.Zero) { acc, line -> acc + line.unitPrice * line.qty }
    val itemDiscountTotal: Money get() = grossSubtotal - subtotal
    val receivedNum: Double get() = received.toDoubleOrNull() ?: 0.0
    val change: Money get() = (Money(receivedNum) - total).coerceAtLeast(Money.Zero)
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
