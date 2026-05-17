package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.SettingsDto
import app.devper.pharm.data.remote.dto.SettingsInputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class SettingsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {
    suspend fun get(): SettingsDto =
        client.get(config.pharmacy("/settings")).body()

    suspend fun put(request: SettingsInputDto): SettingsDto =
        client.put(config.pharmacy("/settings")) { setBody(request) }.body()
}
