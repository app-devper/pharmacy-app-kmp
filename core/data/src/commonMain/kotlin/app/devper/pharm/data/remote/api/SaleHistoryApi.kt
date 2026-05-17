package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.DrugReturnDto
import app.devper.pharm.data.remote.dto.DrugReturnRequest
import app.devper.pharm.data.remote.dto.SaleItemDto
import app.devper.pharm.data.remote.dto.SaleSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SaleHistoryApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {
    suspend fun list(
        from: String?,
        to: String?,
        query: String?,
        limit: Int,
    ): List<SaleSummaryDto> = client.get(config.pharmacy("/sales")) {
        if (!from.isNullOrBlank()) parameter("from", from)
        if (!to.isNullOrBlank()) parameter("to", to)
        if (!query.isNullOrBlank()) parameter("q", query)
        parameter("limit", limit)
    }.body()

    suspend fun items(saleId: String): List<SaleItemDto> =
        client.get(config.pharmacy("/sales/$saleId/items")).body()

    suspend fun returns(saleId: String): List<DrugReturnDto> =
        client.get(config.pharmacy("/sales/$saleId/returns")).body()

    suspend fun submitReturn(saleId: String, request: DrugReturnRequest) {
        client.post(config.pharmacy("/sales/$saleId/return")) { setBody(request) }
    }
}
