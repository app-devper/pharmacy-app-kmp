package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.CustomerDto
import app.devper.pharm.data.remote.dto.CustomerInputDto
import app.devper.pharm.data.remote.dto.SaleSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class CustomerApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(): List<CustomerDto> = client.get(config.pharmacy("/customers")).body()

    suspend fun add(request: CustomerInputDto): CustomerDto =
        client.post(config.pharmacy("/customers")) { setBody(request) }.body()

    suspend fun update(id: String, request: CustomerInputDto) {
        client.put(config.pharmacy("/customers/$id")) { setBody(request) }
    }

    suspend fun getSales(id: String): List<SaleSummaryDto> =
        client.get(config.pharmacy("/customers/$id/sales")).body()
}
