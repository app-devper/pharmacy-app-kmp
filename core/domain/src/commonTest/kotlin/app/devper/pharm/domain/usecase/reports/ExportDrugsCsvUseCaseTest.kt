@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.ky.ExportKyFormParam
import app.devper.pharm.domain.param.reports.ExportDrugsCsvParam
import app.devper.pharm.domain.repository.ky.ExportRepository
import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.domain.usecase.reports.ExportDrugsCsvUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportDrugsCsvUseCaseTest {

    private class CapturingExportRepository : ExportRepository {
        var lastFilename: String? = null
        var lastBytes: ByteArray? = null

        override suspend fun exportKyForm(param: ExportKyFormParam): String = error("not used")

        override suspend fun saveCsv(filename: String, bytes: ByteArray): String {
            lastFilename = filename
            lastBytes = bytes
            return "saved $filename"
        }
    }

    private fun drug(id: String, name: String, stock: Int) = Drug(
        id = id, name = name, genericName = "Generic $id", type = "cur", strength = null,
        barcode = "88500$id", sellPrice = Money(8.0), costPrice = Money(4.0),
        stock = Quantity(stock), minStock = Quantity(10), unit = "เม็ด", regNo = "1A/$id",
    )

    @Test
    fun exports_rows_with_default_headers_and_row_count_filename() = runTest {
        val repo = CapturingExportRepository()
        val result = ExportDrugsCsvUseCase(repo, testDispatchers())(
            ExportDrugsCsvParam(rows = listOf(drug("1", "Amlodipine", 120), drug("2", "Metformin", 40))),
        )
        assertTrue(result.isSuccess)
        assertEquals("drugs_2.csv", repo.lastFilename)
        val csv = repo.lastBytes!!.decodeToString().removePrefix("\uFEFF")
        assertTrue(csv.lineSequence().first().startsWith("name,generic"))
        assertTrue(csv.contains("Amlodipine"))
        assertTrue(csv.contains("Metformin"))
    }

    @Test
    fun uses_provided_localized_headers() = runTest {
        val repo = CapturingExportRepository()
        ExportDrugsCsvUseCase(repo, testDispatchers())(
            ExportDrugsCsvParam(
                rows = listOf(drug("1", "Amlodipine", 5)),
                headers = listOf("ชื่อยา", "ชื่อสามัญ"),
            ),
        )
        val csv = repo.lastBytes!!.decodeToString().removePrefix("\uFEFF")
        assertTrue(csv.lineSequence().first().startsWith("ชื่อยา,ชื่อสามัญ"))
    }
}
