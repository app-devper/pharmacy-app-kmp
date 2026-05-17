package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsBytes

class ExportApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun exportKyForm(form: String, month: String): ByteArray =
        client.get(config.pharmacy("/export/$form")) {
            if (month.isNotBlank()) parameter("month", month)
        }.bodyAsBytes()
}
