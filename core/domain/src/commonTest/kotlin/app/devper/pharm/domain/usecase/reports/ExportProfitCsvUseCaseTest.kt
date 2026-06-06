@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.domain.model.DrugProfit
import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.ExportProfitCsvParam
import app.devper.pharm.domain.repository.ExportRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportProfitCsvUseCaseTest {

    private class CapturingExportRepository : ExportRepository {
        var lastFilename: String? = null
        var lastBytes: ByteArray? = null

        override suspend fun exportKyForm(param: ExportKyFormParam): String =
            error("not used in these tests")

        override suspend fun saveCsv(filename: String, bytes: ByteArray): String {
            lastFilename = filename
            lastBytes = bytes
            return "saved $filename"
        }
    }

    private fun useCase() = ExportProfitCsvUseCase(CapturingExportRepository(), testDispatchers())

    private fun row(margin: Double, profit: Double = 100.0): DrugProfit = DrugProfit(
        drugId = "d", drugName = "Paracetamol",
        qtySold = 10, revenue = 1000.0, cost = 1000.0 - profit, profit = profit, margin = margin,
    )

    private fun bodyOf(bytes: ByteArray): String =
        String(bytes, charset = Charsets.UTF_8).substring(3)

    @Test
    fun negative_margin_formats_correctly() = runTest {
        val repo = CapturingExportRepository()
        val uc = ExportProfitCsvUseCase(repo, testDispatchers())
        uc(ExportProfitCsvParam(from = "", to = "", rows = listOf(row(margin = -50.0, profit = -150.0))))
        val csv = bodyOf(repo.lastBytes!!)
        assertTrue("-50.00%" in csv, "expected -50.00% in CSV, got: $csv")
        assertTrue("-150.00" in csv, "expected -150.00 profit, got: $csv")
    }

    @Test
    fun positive_margin_formats_with_two_decimals() = runTest {
        val repo = CapturingExportRepository()
        val uc = ExportProfitCsvUseCase(repo, testDispatchers())
        uc(ExportProfitCsvParam(from = "", to = "", rows = listOf(row(margin = 25.5))))
        val csv = bodyOf(repo.lastBytes!!)
        assertTrue("25.50%" in csv, "expected 25.50% in CSV, got: $csv")
    }

    @Test
    fun zero_margin_formats_zero() = runTest {
        val repo = CapturingExportRepository()
        val uc = ExportProfitCsvUseCase(repo, testDispatchers())
        uc(ExportProfitCsvParam(from = "", to = "", rows = listOf(row(margin = 0.0, profit = 0.0))))
        val csv = bodyOf(repo.lastBytes!!)
        assertTrue("0.00%" in csv, "expected 0.00% in CSV, got: $csv")
    }

    @Test
    fun filename_uses_all_when_range_is_blank() = runTest {
        val repo = CapturingExportRepository()
        val uc = ExportProfitCsvUseCase(repo, testDispatchers())
        uc(ExportProfitCsvParam(from = "", to = "", rows = emptyList()))
        assertEquals("profit_all.csv", repo.lastFilename)
    }

    @Test
    fun filename_uses_single_date_when_from_equals_to() = runTest {
        val repo = CapturingExportRepository()
        val uc = ExportProfitCsvUseCase(repo, testDispatchers())
        uc(ExportProfitCsvParam(from = "2026-05-01", to = "2026-05-01", rows = emptyList()))
        assertEquals("profit_2026-05-01.csv", repo.lastFilename)
    }
}
