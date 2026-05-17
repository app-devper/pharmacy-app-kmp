package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.Ky10Dto
import app.devper.pharm.data.remote.dto.Ky10Request
import app.devper.pharm.data.remote.dto.Ky11Dto
import app.devper.pharm.data.remote.dto.Ky11Request
import app.devper.pharm.data.remote.dto.Ky12Dto
import app.devper.pharm.data.remote.dto.Ky12Request
import app.devper.pharm.data.remote.dto.Ky9Dto
import app.devper.pharm.data.remote.dto.Ky9Request
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KyApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun submitKy10(request: Ky10Request) {
        client.post(config.pharmacy("/ky10")) { setBody(request) }
    }

    suspend fun submitKy11(request: Ky11Request) {
        client.post(config.pharmacy("/ky11")) { setBody(request) }
    }

    suspend fun submitKy12(request: Ky12Request) {
        client.post(config.pharmacy("/ky12")) { setBody(request) }
    }

    suspend fun addKy9(request: Ky9Request) {
        client.post(config.pharmacy("/ky9")) { setBody(request) }
    }

    suspend fun listKy9(month: String): List<Ky9Dto> =
        client.get(config.pharmacy("/ky9")) {
            if (month.isNotBlank()) parameter("month", month)
        }.body()

    suspend fun listKy10(month: String): List<Ky10Dto> =
        client.get(config.pharmacy("/ky10")) {
            if (month.isNotBlank()) parameter("month", month)
        }.body()

    suspend fun listKy11(month: String): List<Ky11Dto> =
        client.get(config.pharmacy("/ky11")) {
            if (month.isNotBlank()) parameter("month", month)
        }.body()

    suspend fun listKy12(month: String): List<Ky12Dto> =
        client.get(config.pharmacy("/ky12")) {
            if (month.isNotBlank()) parameter("month", month)
        }.body()
}
