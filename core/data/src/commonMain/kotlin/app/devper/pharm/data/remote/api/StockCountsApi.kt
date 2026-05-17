package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.StockCountDto
import app.devper.pharm.data.remote.dto.StockCountInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class StockCountsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(limit: Int): List<StockCountDto> =
        client.get(config.pharmacy("/stock-counts")) {
            parameter("limit", limit)
        }.body()

    suspend fun add(request: StockCountInputDto): StockCountDto =
        client.post(config.pharmacy("/stock-counts")) { setBody(request) }.body()
}
