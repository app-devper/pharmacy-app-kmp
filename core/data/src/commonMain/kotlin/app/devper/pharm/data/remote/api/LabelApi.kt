package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.PrintLabelsRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LabelApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun printLabels(request: PrintLabelsRequest): ByteArray =
        client.post(config.pharmacy("/labels/print")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyAsBytes()
}
