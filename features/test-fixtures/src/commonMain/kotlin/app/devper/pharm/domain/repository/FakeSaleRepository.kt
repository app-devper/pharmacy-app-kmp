package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.sales.SaleRepository

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.domain.model.AbandonOutcome
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.sales.VoidSaleParam

class FakeSaleRepository(
    private val successResult: Sale = Sale(
        id = "sale-1",
        billNo = "INV-260510-001",
        total = Money(0.0),
        change = Money(0.0),
        discount = Money(0.0),
        stockUpdates = emptyList(),
    ),
    private val checkoutThrows: Throwable? = null,
    private val voidThrows: Throwable? = null,
    var replayThrows: Throwable? = null,
    private val abandonOutcome: AbandonOutcome = AbandonOutcome.Abandoned,
    private val abandonThrows: Throwable? = null,
) : SaleRepository {

    /** (client request id, kind, reason) of each abandonment sent. */
    val abandoned = mutableListOf<Triple<String, String, String>>()

    var lastCheckout: CheckoutParam? = null
        private set
    var lastVoid: VoidSaleParam? = null
        private set
    var serializeCalls: Int = 0
        private set
    var lastReplay: String? = null
        private set
    var replayCount: Int = 0
        private set

    override suspend fun checkout(param: CheckoutParam): Sale {
        lastCheckout = param
        checkoutThrows?.let { throw it }
        return successResult.copy(
            total = param.items.fold(Money.Zero) { acc, item -> acc + item.unitPrice * item.qty } - param.discount,
        )
    }

    override suspend fun void(param: VoidSaleParam) {
        voidThrows?.let { throw it }
        lastVoid = param
    }

    override fun serializeCheckout(param: CheckoutParam): String {
        serializeCalls++
        return """{"client_request_id":"${param.clientRequestId ?: ""}","items":${param.items.size}}"""
    }

    override suspend fun replayCheckout(payloadJson: String): Sale {
        lastReplay = payloadJson
        replayCount++
        replayThrows?.let { throw it }
        return successResult
    }

    override suspend fun abandonSale(clientRequestId: String, payloadJson: String, reason: String): AbandonOutcome {
        abandonThrows?.let { throw it }
        abandoned += Triple(clientRequestId, "sale", reason)
        return abandonOutcome
    }

    override suspend fun abandonKyForms(clientRequestId: String, forms: List<KyForm>, reason: String) {
        abandonThrows?.let { throw it }
        abandoned += Triple(clientRequestId, "ky_forms", reason)
    }
}
