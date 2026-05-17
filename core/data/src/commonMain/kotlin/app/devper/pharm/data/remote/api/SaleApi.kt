package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.SaleRequest
import app.devper.pharm.data.remote.dto.SaleResponse
import app.devper.pharm.data.remote.dto.VoidSaleRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SaleApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun checkout(request: SaleRequest): SaleResponse =
        client.post(config.pharmacy("/sales")) { setBody(request) }.body()

    suspend fun void(saleId: String, request: VoidSaleRequest) {
        client.post(config.pharmacy("/sales/$saleId/void")) { setBody(request) }
    }
}
