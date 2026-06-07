package app.devper.pharm.data.repository

import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.api.SaleApi
import app.devper.pharm.data.remote.dto.SaleRequest
import app.devper.pharm.data.remote.dto.VoidSaleRequest
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.CheckoutParam
import app.devper.pharm.domain.param.VoidSaleParam
import app.devper.pharm.domain.repository.SaleRepository

class SaleRepositoryImpl(
    private val api: SaleApi,
    private val stockChangeBus: StockChangeBus,
) : SaleRepository {

    private val json = AppJson

    override suspend fun checkout(param: CheckoutParam): Sale {
        val sale = api.checkout(param.toRequest()).toDomain()
        stockChangeBus.emit()
        return sale
    }

    override suspend fun void(param: VoidSaleParam) {
        api.void(param.saleId, VoidSaleRequest(reason = param.reason))
        stockChangeBus.emit()
    }

    override fun serializeCheckout(param: CheckoutParam): String =
        json.encodeToString(SaleRequest.serializer(), param.toRequest())

    override suspend fun replayCheckout(payloadJson: String): Sale {
        val request = json.decodeFromString(SaleRequest.serializer(), payloadJson)
        val sale = api.checkout(request).toDomain()
        stockChangeBus.emit()
        return sale
    }
}
