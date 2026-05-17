package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.StockAdjustmentDto
import app.devper.pharm.data.remote.dto.StockAdjustmentInputDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body

class StockAdjustmentApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(drugId: String): List<StockAdjustmentDto> =
        client.get(config.pharmacy("/drugs/$drugId/adjustments")).body()

    suspend fun add(drugId: String, request: StockAdjustmentInputDto) {
        client.post(config.pharmacy("/drugs/$drugId/adjustments")) { setBody(request) }
    }
}
