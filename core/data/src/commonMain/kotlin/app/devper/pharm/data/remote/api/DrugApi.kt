package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.BulkDrugImportInputDto
import app.devper.pharm.data.remote.dto.BulkImportResultDto
import app.devper.pharm.data.remote.dto.DrugDto
import app.devper.pharm.data.remote.dto.DrugInputDto
import app.devper.pharm.data.remote.dto.DrugUpdateDto
import app.devper.pharm.data.remote.dto.ReorderSuggestionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class DrugApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(): List<DrugDto> = client.get(config.pharmacy("/drugs")).body()

    suspend fun add(request: DrugInputDto): DrugDto =
        client.post(config.pharmacy("/drugs")) { setBody(request) }.body()

    suspend fun update(id: String, request: DrugUpdateDto) {
        client.put(config.pharmacy("/drugs/$id")) { setBody(request) }
    }

    suspend fun bulkImport(request: BulkDrugImportInputDto): BulkImportResultDto =
        client.post(config.pharmacy("/drugs/bulk")) { setBody(request) }.body()

    suspend fun lowStock(): List<DrugDto> =
        client.get(config.pharmacy("/drugs/low-stock")).body()

    suspend fun reorderSuggestions(days: Int?, lookahead: Int?): List<ReorderSuggestionDto> =
        client.get(config.pharmacy("/drugs/reorder-suggestions")) {
            days?.let { parameter("days", it) }
            lookahead?.let { parameter("lookahead", it) }
        }.body()
}
