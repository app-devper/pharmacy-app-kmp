package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.CheckoutParam
import app.devper.pharm.domain.param.VoidSaleParam

class FakeSaleRepository(
    private val successResult: Sale = Sale(
        id = "sale-1",
        billNo = "INV-260510-001",
        total = 0.0,
        change = 0.0,
        discount = 0.0,
        stockUpdates = emptyList(),
    ),
    private val checkoutThrows: Throwable? = null,
    private val voidThrows: Throwable? = null,
    private val replayThrows: Throwable? = null,
) : SaleRepository {

    var lastCheckout: CheckoutParam? = null
        private set
    var lastVoid: VoidSaleParam? = null
        private set
    var serializeCalls: Int = 0
        private set
    var lastReplay: String? = null
        private set

    override suspend fun checkout(param: CheckoutParam): Sale {
        lastCheckout = param
        checkoutThrows?.let { throw it }
        return successResult.copy(
            total = param.items.sumOf { it.unitPrice * it.qty } - param.discount,
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
        replayThrows?.let { throw it }
        return successResult
    }
}
