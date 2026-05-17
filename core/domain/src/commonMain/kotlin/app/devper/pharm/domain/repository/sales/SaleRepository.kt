package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.CheckoutParam
import app.devper.pharm.domain.param.VoidSaleParam

interface SaleRepository {
    suspend fun checkout(param: CheckoutParam): Sale

    suspend fun void(param: VoidSaleParam)

    fun serializeCheckout(param: CheckoutParam): String

    suspend fun replayCheckout(payloadJson: String): Sale
}
