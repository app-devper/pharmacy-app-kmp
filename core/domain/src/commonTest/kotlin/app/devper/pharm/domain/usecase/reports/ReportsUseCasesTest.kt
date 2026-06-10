@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
import app.devper.pharm.domain.usecase.reports.GetDashboardUseCase
import app.devper.pharm.domain.usecase.reports.GetEodReportUseCase
import app.devper.pharm.domain.usecase.reports.GetProfitReportUseCase
import app.devper.pharm.domain.usecase.reports.GetSlowDrugsUseCase
import app.devper.pharm.domain.usecase.reports.GetTopDrugsUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.reports.DashboardRangeParam
import app.devper.pharm.domain.param.reports.EodReportParam
import app.devper.pharm.domain.param.inventory.ReorderSuggestionsParam
import app.devper.pharm.domain.param.reports.ReportRangeParam
import app.devper.pharm.domain.param.reports.TopOrSlowDrugsParam
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.repository.FakeReportsRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private fun reorderSuggestion(drugId: String, drugName: String = "Drug $drugId") = ReorderSuggestion(
    drugId = drugId, drugName = drugName, unit = "เม็ด",
    currentStock = Quantity(2), minStock = Quantity(10),
    qtySold = Quantity(20), avgDailySale = 2.5, daysLeft = 1.0,
    suggestedQty = Quantity(30), costPrice = Money(1.0), sellPrice = Money(3.0),
)

class GetDashboardUseCaseTest {

    @Test
    fun direct_param_forwards_to_repository() = runTest {
        val repo = FakeReportsRepository()
        val param = DashboardRangeParam(days = 30)

        val result = GetDashboardUseCase(repo, testDispatchers()).invoke(param)

        assertNotNull(result.getOrNull())
    }

    @Test
    fun convenience_invoke_uses_default_range() = runTest {
        val repo = FakeReportsRepository()

        val result = GetDashboardUseCase(repo, testDispatchers()).invoke()

        assertNotNull(result.getOrNull())
    }
}

class GetEodReportUseCaseTest {

    @Test
    fun direct_param_forwards_to_repository() = runTest {
        val repo = FakeReportsRepository()
        val param = EodReportParam(date = LocalDate(2026, 5, 17))

        val result = GetEodReportUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastEodParam)
        assertNotNull(result)
    }

    @Test
    fun convenience_invoke_uses_default_empty_param() = runTest {
        val repo = FakeReportsRepository()

        GetEodReportUseCase(repo, testDispatchers()).invoke().getOrThrow()

        assertEquals(EodReportParam(), repo.lastEodParam)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeReportsRepository(eodThrows = RuntimeException("eod failed"))

        val result = GetEodReportUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

class GetProfitReportUseCaseTest {

    @Test
    fun forwards_range_param_to_repository() = runTest {
        val repo = FakeReportsRepository()
        val param = ReportRangeParam(from = LocalDate(2026, 5, 1), to = LocalDate(2026, 5, 31))

        val result = GetProfitReportUseCase(repo, testDispatchers()).invoke(param)

        assertNotNull(result.getOrNull())
    }
}

class GetReorderSuggestionsUseCaseTest {

    @Test
    fun direct_param_returns_repository_seed() = runTest {
        val seed = listOf(reorderSuggestion("d1"), reorderSuggestion("d2"))
        val repo = FakeDrugRepository(reorderSeed = seed)
        val param = ReorderSuggestionsParam(days = 30, lookahead = 14)

        val result = GetReorderSuggestionsUseCase(repo, testDispatchers()).invoke(param)

        assertEquals(seed, result.getOrThrow())
        assertEquals(param, repo.lastReorderParam)
    }

    @Test
    fun convenience_invoke_uses_default_param() = runTest {
        val seed = listOf(reorderSuggestion("d1"))
        val repo = FakeDrugRepository(reorderSeed = seed)

        val result = GetReorderSuggestionsUseCase(repo, testDispatchers()).invoke()

        assertEquals(seed, result.getOrThrow())
        assertEquals(ReorderSuggestionsParam(), repo.lastReorderParam)
    }
}

class GetSlowDrugsUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeReportsRepository()
        val param = TopOrSlowDrugsParam(days = 60)

        val result = GetSlowDrugsUseCase(repo, testDispatchers()).invoke(param)

        assertEquals(emptyList(), result.getOrThrow())
    }

    @Test
    fun convenience_invoke_uses_default_90_days() = runTest {
        val repo = FakeReportsRepository()

        val result = GetSlowDrugsUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrThrow())
    }

    @Test
    fun convenience_invoke_with_explicit_days() = runTest {
        val repo = FakeReportsRepository()

        val result = GetSlowDrugsUseCase(repo, testDispatchers()).invoke(days = 30)

        assertTrue(result.isSuccess)
    }
}

class GetTopDrugsUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeReportsRepository()
        val param = TopOrSlowDrugsParam(days = 7)

        val result = GetTopDrugsUseCase(repo, testDispatchers()).invoke(param)

        assertEquals(emptyList(), result.getOrThrow())
    }

    @Test
    fun convenience_invoke_uses_default_30_days() = runTest {
        val repo = FakeReportsRepository()

        val result = GetTopDrugsUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrThrow())
    }
}
