@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.ExportMovementsCsvParam
import app.devper.pharm.domain.repository.ExportRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportMovementsCsvUseCaseTest {

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

    private fun useCase(repo: ExportRepository = CapturingExportRepository()) =
        ExportMovementsCsvUseCase(repo, testDispatchers()) to repo

    private fun sampleRows(): List<StockMovement> = listOf(
        StockMovement(
            id = "m1",
            type = MovementType.Sale,
            drugId = "d1",
            drugName = "Paracetamol 500mg",
            delta = -2,
            reference = "BILL-001",
            note = "",
            at = "2026-05-17T09:00",
        ),
    )

    @Test
    fun filename_uses_all_when_range_is_blank() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = null, to = null, drugName = "", rows = sampleRows()))
        assertEquals("movements_all.csv", (repo as CapturingExportRepository).lastFilename)
    }

    @Test
    fun filename_uses_single_date_when_from_equals_to() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = kotlinx.datetime.LocalDate.parse("2026-05-17"), to = kotlinx.datetime.LocalDate.parse("2026-05-17"), drugName = "", rows = sampleRows()))
        assertEquals("movements_2026-05-17.csv", (repo as CapturingExportRepository).lastFilename)
    }

    @Test
    fun filename_uses_range_when_from_and_to_differ() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = kotlinx.datetime.LocalDate.parse("2026-05-01"), to = kotlinx.datetime.LocalDate.parse("2026-05-31"), drugName = "", rows = sampleRows()))
        assertEquals("movements_2026-05-01_2026-05-31.csv", (repo as CapturingExportRepository).lastFilename)
    }

    @Test
    fun filename_uses_any_placeholder_when_one_endpoint_blank() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = kotlinx.datetime.LocalDate.parse("2026-05-01"), to = null, drugName = "", rows = sampleRows()))
        assertEquals("movements_2026-05-01_any.csv", (repo as CapturingExportRepository).lastFilename)
    }

    @Test
    fun filename_appends_normalized_drug_slug() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = null, to = null, drugName = "Paracetamol 500mg", rows = sampleRows()))
        assertEquals("movements_all_paracetamol-500mg.csv", (repo as CapturingExportRepository).lastFilename)
    }

    @Test
    fun filename_strips_leading_trailing_non_alphanumeric_from_drug_slug() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = null, to = null, drugName = "  ยา--ดี!!  ", rows = sampleRows()))
        val name = (repo as CapturingExportRepository).lastFilename!!
        assertTrue(name.startsWith("movements_all_"))
        assertTrue(name.endsWith(".csv"))
        assertTrue(!name.contains("!"))
    }

    @Test
    fun filename_caps_drug_slug_at_40_chars() = runTest {
        val (uc, repo) = useCase()
        val longName = "abcdefghij".repeat(8)
        uc(ExportMovementsCsvParam(from = null, to = null, drugName = longName, rows = sampleRows()))
        val name = (repo as CapturingExportRepository).lastFilename!!
        val slug = name.removePrefix("movements_all_").removeSuffix(".csv")
        assertTrue(slug.length <= 40, "slug too long: ${slug.length} ($slug)")
    }

    @Test
    fun bytes_payload_starts_with_utf8_bom() = runTest {
        val (uc, repo) = useCase()
        uc(ExportMovementsCsvParam(from = null, to = null, drugName = "", rows = sampleRows()))
        val bytes = (repo as CapturingExportRepository).lastBytes!!
        assertEquals(0xEF.toByte(), bytes[0])
        assertEquals(0xBB.toByte(), bytes[1])
        assertEquals(0xBF.toByte(), bytes[2])
    }
}
