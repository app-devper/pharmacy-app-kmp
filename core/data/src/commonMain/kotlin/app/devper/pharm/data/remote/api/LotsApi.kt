package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.DrugLotDto
import app.devper.pharm.data.remote.dto.DrugLotInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LotsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(drugId: String): List<DrugLotDto> =
        client.get(config.pharmacy("/drugs/$drugId/lots")).body()

    suspend fun add(drugId: String, request: DrugLotInputDto): DrugLotDto =
        client.post(config.pharmacy("/drugs/$drugId/lots")) { setBody(request) }.body()

    suspend fun delete(drugId: String, lotId: String) {
        client.delete(config.pharmacy("/drugs/$drugId/lots/$lotId"))
    }
}
