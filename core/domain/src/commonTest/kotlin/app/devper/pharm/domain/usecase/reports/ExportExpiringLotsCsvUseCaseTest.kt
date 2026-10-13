package app.devper.pharm.domain.usecase.reports

import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.param.reports.ExportExpiringLotsCsvParam
import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportExpiringLotsCsvUseCaseTest {

    private val lot = ExpiringLot(
        id = "l1", drugId = "d1", drugName = "Paracetamol 500mg", lotNumber = "A1",
        expiryDate = LocalDate(2026, 11, 30), remaining = 12, daysLeft = 48,
    )

    @Test
    fun saves_the_lots_shown_with_the_window_in_the_file_name() = runTest {
        val export = FakeExportRepository()
        val result = ExportExpiringLotsCsvUseCase(export, testDispatchers())
            .invoke(ExportExpiringLotsCsvParam(windowTag = "Within60", rows = listOf(lot), headers = listOf("ยา", "ล็อต", "หมดอายุ", "คงเหลือ", "เหลือ (วัน)")))

        assertTrue(result.isSuccess)
        assertEquals("expiry_within60.csv", export.lastFilename)
        val csv = export.lastBytes!!.decodeToString()
        assertTrue("ยา,ล็อต,หมดอายุ,คงเหลือ,เหลือ (วัน)" in csv, csv)
        assertTrue("Paracetamol 500mg,A1,2026-11-30,12,48" in csv, csv)
    }
}
