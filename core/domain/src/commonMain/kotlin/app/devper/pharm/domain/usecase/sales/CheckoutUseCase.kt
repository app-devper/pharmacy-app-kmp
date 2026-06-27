package app.devper.pharm.domain.usecase.sales

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.domain.validation.SaleValidationError

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CheckoutFailure
import app.devper.pharm.domain.model.CheckoutOutcome
import app.devper.pharm.domain.model.OversellShortfall
import app.devper.pharm.domain.param.sales.CheckoutLineParam
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.sales.RunCheckoutParam
import app.devper.pharm.domain.repository.sales.CartRepository
import app.devper.pharm.domain.repository.sales.SaleRepository

class CheckoutUseCase(
    private val cart: CartRepository,
    private val sales: SaleRepository,
    dispatchers: AppDispatchers,
) : BaseUseCase<RunCheckoutParam, CheckoutOutcome>(dispatchers) {

    suspend operator fun invoke(
        received: Money,
        allowOversell: Boolean = false,
        clientRequestId: String? = null,
        kySkippedByCashier: Boolean = false,
    ): Result<CheckoutOutcome> = invoke(
        RunCheckoutParam(received, allowOversell, clientRequestId, kySkippedByCashier),
    )

    override suspend fun execute(param: RunCheckoutParam): CheckoutOutcome {
        val snapshot = cart.state.value.active
        val lines = snapshot.items
        if (lines.isEmpty()) {
            throw CheckoutFailure(SaleValidationError.EmptyCart())
        }

        if (!param.allowOversell) {
            val shortfalls = computeShortfalls(lines)
            if (shortfalls.isNotEmpty()) {
                return CheckoutOutcome.NeedsOversellConfirm(shortfalls)
            }
        }

        val customer = snapshot.customer
        val tier = snapshot.activeTier
        val cartDiscount = snapshot.cartDiscount
        val subtotal = lines.fold(Money.Zero) { acc, line -> acc + line.lineTotal }
        val discountAmount = cartDiscount.apply(subtotal)

        val oversoldDrugIds = if (param.allowOversell) {
            computeShortfalls(lines).map { it.drugId }.toSet()
        } else emptySet()

        val checkoutParam = CheckoutParam(
            items = lines.map { line ->
                CheckoutLineParam(
                    drugId = line.drug.id,
                    qty = line.qty,
                    unitPrice = (line.basePrice - line.discount).coerceAtLeast(Money.Zero),
                    originalUnitPrice = line.basePrice,
                    itemDiscount = line.discount,
                    priceTier = tier.takeIf { it.isNotBlank() } ?: "",
                    allowOversell = line.drug.id in oversoldDrugIds,
                    unit = line.selectedUnit?.name.orEmpty(),
                    unitFactor = line.factor,
                )
            },
            received = param.received,
            customerId = customer?.id,
            discount = discountAmount,
            priceTier = tier,
            clientRequestId = param.clientRequestId,
            kySkippedByCashier = param.kySkippedByCashier,
        )

        val serialized = serializeForOfflineQueue(checkoutParam)
        val sale = try {
            sales.checkout(checkoutParam)
        } catch (e: Exception) {
            throw CheckoutFailure(
                cause = e,
                serializedRequest = serialized,
                clientRequestId = param.clientRequestId,
            )
        }
        cart.commitReceipt(sale)
        return CheckoutOutcome.Success(sale)
    }

    private fun serializeForOfflineQueue(param: CheckoutParam): String? = try {
        sales.serializeCheckout(param)
    } catch (e: Exception) {
        null
    }

    private fun computeShortfalls(lines: List<CartLine>): List<OversellShortfall> =
        lines
            .groupBy { it.drug.id }
            .mapNotNull { (_, group) ->
                val drug = group.first().drug
                val totalQty = group.sumOf { it.qty }
                val available = drug.stock.value.coerceAtLeast(0)
                if (totalQty > available) {
                    OversellShortfall(drug.id, drug.name, totalQty, available)
                } else null
            }
}
