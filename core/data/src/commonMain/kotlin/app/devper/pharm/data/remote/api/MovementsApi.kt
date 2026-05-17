package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.MovementsPageDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MovementsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(
        from: String?,
        to: String?,
        drugName: String?,
        types: List<String>,
        limit: Int,
        offset: Int,
    ): MovementsPageDto = client.get(config.pharmacy("/movements")) {
        if (!from.isNullOrBlank()) parameter("from", from)
        if (!to.isNullOrBlank()) parameter("to", to)
        if (!drugName.isNullOrBlank()) parameter("drug_name", drugName)
        if (types.isNotEmpty()) parameter("types", types.joinToString(","))
        parameter("limit", limit)
        parameter("offset", offset)
    }.body()
}
