package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.DashboardDto
import app.devper.pharm.data.remote.dto.EodReportDto
import app.devper.pharm.data.remote.dto.ProfitReportDto
import app.devper.pharm.data.remote.dto.SlowDrugDto
import app.devper.pharm.data.remote.dto.TopDrugDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ReportsApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun dashboard(days: Int): DashboardDto =
        client.get(config.pharmacy("/report/dashboard")) {
            parameter("days", days)
        }.body()

    suspend fun topDrugs(days: Int): List<TopDrugDto> =
        client.get(config.pharmacy("/report/top-drugs")) {
            parameter("days", days)
        }.body()

    suspend fun slowDrugs(days: Int): List<SlowDrugDto> =
        client.get(config.pharmacy("/report/slow-drugs")) {
            parameter("days", days)
        }.body()

    suspend fun profit(from: String, to: String): ProfitReportDto =
        client.get(config.pharmacy("/report/profit")) {
            if (from.isNotBlank()) parameter("from", from)
            if (to.isNotBlank()) parameter("to", to)
        }.body()

    suspend fun eod(date: String): EodReportDto =
        client.get(config.pharmacy("/report/eod")) {
            if (date.isNotBlank()) parameter("date", date)
        }.body()
}
