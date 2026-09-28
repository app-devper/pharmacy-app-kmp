package app.devper.pharm.data.repository

import app.devper.pharm.common.ConflictException
import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.dto.AbandonRequest
import app.devper.pharm.data.remote.dto.AlreadyRecordedResponse
import app.devper.pharm.data.storage.PendingKyFormDto
import app.devper.pharm.data.storage.toPendingDto
import app.devper.pharm.data.remote.api.SaleApi
import app.devper.pharm.data.remote.dto.SaleRequest
import app.devper.pharm.data.remote.dto.VoidSaleRequest
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.AbandonOutcome
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.sales.VoidSaleParam
import app.devper.pharm.domain.repository.sales.SaleRepository
import kotlinx.serialization.builtins.ListSerializer

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

    override suspend fun abandonSale(clientRequestId: String, payloadJson: String, reason: String): AbandonOutcome {
        val request = AbandonRequest(
            clientRequestId = clientRequestId,
            kind = "sale",
            payload = json.parseToJsonElement(payloadJson),
            reason = reason,
        )
        return try {
            api.abandon(request)
            AbandonOutcome.Abandoned
        } catch (e: ConflictException) {
            val recorded = e.payload
                ?.let { runCatching { json.decodeFromString(AlreadyRecordedResponse.serializer(), it) }.getOrNull() }
                ?.sale
                ?: throw e
            AbandonOutcome.AlreadyRecorded(recorded.toDomain())
        }
    }

    override suspend fun abandonKyForms(clientRequestId: String, forms: List<KyForm>, reason: String) {
        api.abandon(
            AbandonRequest(
                clientRequestId = clientRequestId,
                kind = "ky_forms",
                payload = json.encodeToJsonElement(ListSerializer(PendingKyFormDto.serializer()), forms.map { it.toPendingDto() }),
                reason = reason,
            ),
        )
    }
}
