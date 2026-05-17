package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.PurchaseOrderDto
import app.devper.pharm.data.remote.dto.PurchaseOrderInputDto
import app.devper.pharm.data.remote.dto.PurchaseOrderSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class PurchaseOrderApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(): List<PurchaseOrderSummaryDto> =
        client.get(config.pharmacy("/imports")).body()

    suspend fun get(id: String): PurchaseOrderDto =
        client.get(config.pharmacy("/imports/$id")).body()

    suspend fun add(request: PurchaseOrderInputDto): PurchaseOrderDto =
        client.post(config.pharmacy("/imports")) { setBody(request) }.body()

    suspend fun update(id: String, request: PurchaseOrderInputDto): PurchaseOrderDto =
        client.put(config.pharmacy("/imports/$id")) { setBody(request) }.body()

    suspend fun confirm(id: String): PurchaseOrderDto =
        client.post(config.pharmacy("/imports/$id/confirm")).body()

    suspend fun delete(id: String) {
        client.delete(config.pharmacy("/imports/$id"))
    }
}
