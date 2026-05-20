@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.AppException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.domain.model.Dashboard
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.ProfitReport
import app.devper.pharm.domain.model.ProfitSummary
import app.devper.pharm.domain.model.ReportSummary
import app.devper.pharm.domain.model.SlowDrug
import app.devper.pharm.domain.model.TopDrug
import app.devper.pharm.domain.param.CloseEodParam
import app.devper.pharm.domain.param.DashboardRangeParam
import app.devper.pharm.domain.param.EodReportParam
import app.devper.pharm.domain.param.ReportRangeParam
import app.devper.pharm.domain.param.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.ReportsRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class StubReportsRepository(
    private val closeResult: EodCloseResult? = null,
    private val closeThrows: Throwable? = null,
) : ReportsRepository {
    var lastCloseParam: CloseEodParam? = null
        private set

    override suspend fun dashboard(param: DashboardRangeParam): Dashboard = Dashboard(
        summary = ReportSummary(0.0, 0, 0.0, 0.0, 0, 0),
        daily = emptyList(),
        monthly = emptyList(),
        recentSales = emptyList(),
    )

    override suspend fun topDrugs(param: TopOrSlowDrugsParam): List<TopDrug> = emptyList()
    override suspend fun slowDrugs(param: TopOrSlowDrugsParam): List<SlowDrug> = emptyList()
    override suspend fun profit(param: ReportRangeParam): ProfitReport = ProfitReport(
        summary = ProfitSummary(0.0, 0.0, 0.0, 0.0, 0),
        byDrug = emptyList(),
    )

    override suspend fun eod(param: EodReportParam): EodReport = EodReport(
        date = param.date,
        billCount = 0,
        totalSales = 0.0,
        totalDiscount = 0.0,
        totalReceived = 0.0,
        totalChange = 0.0,
        netCash = 0.0,
        bills = emptyList(),
    )

    override suspend fun closeEod(param: CloseEodParam): EodCloseResult {
        lastCloseParam = param
        closeThrows?.let { throw it }
        return closeResult ?: error("close result not provided")
    }
}

class CloseEodUseCaseTest {

    private fun dispatchers(): AppDispatchers {
        val one = UnconfinedTestDispatcher()
        return AppDispatchers(main = one, io = one, default = one)
    }

    private val sampleReport = EodReport(
        date = "2026-05-19",
        billCount = 2,
        totalSales = 100.0,
        totalDiscount = 0.0,
        totalReceived = 100.0,
        totalChange = 0.0,
        netCash = 100.0,
        bills = emptyList(),
    )

    private val sampleClose = EodCloseResult(
        closeId = "eod-2026-05-19",
        date = "2026-05-19",
        closedAt = "2026-05-19T23:59:00+07:00",
        closedBy = "user-1",
        report = sampleReport,
    )

    @Test
    fun happy_path_returns_repo_result_and_passes_param_through() = runTest {
        val repo = StubReportsRepository(closeResult = sampleClose)
        val uc = CloseEodUseCase(repo, dispatchers())
        val outcome = uc(CloseEodParam(date = "2026-05-19"))
        assertTrue(outcome.isSuccess)
        assertEquals(sampleClose, outcome.getOrThrow())
        assertEquals("2026-05-19", repo.lastCloseParam?.date)
    }

    @Test
    fun network_failure_propagates_as_typed_AppException_in_Result_failure() = runTest {
        val repo = StubReportsRepository(closeThrows = NetworkException())
        val uc = CloseEodUseCase(repo, dispatchers())
        val outcome = uc(CloseEodParam(date = "2026-05-19"))
        assertTrue(outcome.isFailure)
        val e = outcome.exceptionOrNull()
        assertTrue(e is AppException, "expected AppException, got $e")
        assertTrue(e is NetworkException)
    }

    @Test
    fun conflict_failure_propagates_as_typed_ConflictException_in_Result_failure() = runTest {
        val repo = StubReportsRepository(closeThrows = ConflictException(message = "already closed"))
        val uc = CloseEodUseCase(repo, dispatchers())
        val outcome = uc(CloseEodParam(date = "2026-05-19"))
        assertTrue(outcome.isFailure)
        val e = outcome.exceptionOrNull()
        assertTrue(e is ConflictException)
        assertEquals("already closed", e.message)
    }
}
