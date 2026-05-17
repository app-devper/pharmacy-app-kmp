package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.network.AppJson
import app.devper.pharm.common.ConflictException
import app.devper.pharm.data.remote.dto.ExpiringLotDto
import app.devper.pharm.data.remote.dto.WriteoffLotsInputDto
import app.devper.pharm.data.remote.dto.WriteoffResultDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ExpiringLotsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(daysAhead: Int?, expiredOnly: Boolean): List<ExpiringLotDto> =
        client.get(config.pharmacy("/lots/expiring")) {
            if (expiredOnly) parameter("expired_only", "true")
            else daysAhead?.let { parameter("days", it) }
        }.body()

    suspend fun writeoff(request: WriteoffLotsInputDto): WriteoffResultDto = try {
        client.post(config.pharmacy("/lots/writeoff")) { setBody(request) }.body()
    } catch (e: ConflictException) {
        val payload = e.payload ?: throw e
        AppJson.decodeFromString(WriteoffResultDto.serializer(), payload)
    }
}
