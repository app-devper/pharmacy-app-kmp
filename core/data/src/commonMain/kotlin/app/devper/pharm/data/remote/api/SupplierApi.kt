package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.SupplierDto
import app.devper.pharm.data.remote.dto.SupplierInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class SupplierApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(): List<SupplierDto> =
        client.get(config.pharmacy("/suppliers")).body()

    suspend fun add(request: SupplierInputDto): SupplierDto =
        client.post(config.pharmacy("/suppliers")) { setBody(request) }.body()

    suspend fun update(id: String, request: SupplierInputDto) {
        client.put(config.pharmacy("/suppliers/$id")) { setBody(request) }
    }

    suspend fun delete(id: String) {
        client.delete(config.pharmacy("/suppliers/$id"))
    }
}
