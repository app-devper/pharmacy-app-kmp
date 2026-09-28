package app.devper.pharm.domain.repository.sales

import app.devper.pharm.domain.model.AbandonOutcome
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.sales.VoidSaleParam

interface SaleRepository {
    suspend fun checkout(param: CheckoutParam): Sale

    suspend fun void(param: VoidSaleParam)

    fun serializeCheckout(param: CheckoutParam): String

    suspend fun replayCheckout(payloadJson: String): Sale

    /** Record that the queued sale [clientRequestId] will never be recorded (ADMIN+). */
    suspend fun abandonSale(clientRequestId: String, payloadJson: String, reason: String): AbandonOutcome

    /** Close the refused KY [forms] of the recorded sale [clientRequestId] (ADMIN+). */
    suspend fun abandonKyForms(clientRequestId: String, forms: List<KyForm>, reason: String)
}
