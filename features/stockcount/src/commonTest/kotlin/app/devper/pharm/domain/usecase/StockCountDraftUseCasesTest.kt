@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.StockCountDraft
import app.devper.pharm.domain.repository.FakeStockCountDraftRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private fun testDispatchers() = UnconfinedTestDispatcher().let { d ->
    AppDispatchers(main = d, io = d, default = d)
}

class SaveStockCountDraftUseCaseTest {

    @Test
    fun stores_draft_in_repository() = runTest {
        val repo = FakeStockCountDraftRepository()
        val draft = StockCountDraft(
            counts = mapOf("d1" to "10", "d2" to "5"),
            note = "month-end",
            updatedAt = 123456789L,
        )

        SaveStockCountDraftUseCase(repo, testDispatchers()).invoke(draft).getOrThrow()

        assertEquals(1, repo.saveCallCount)
        assertEquals(draft, repo.stored)
    }

    @Test
    fun empty_draft_overwrites_existing_one() = runTest {
        val repo = FakeStockCountDraftRepository(
            initial = StockCountDraft(counts = mapOf("d1" to "5")),
        )

        SaveStockCountDraftUseCase(repo, testDispatchers()).invoke(StockCountDraft.Empty).getOrThrow()

        assertEquals(StockCountDraft.Empty, repo.stored)
    }
}

class LoadStockCountDraftUseCaseTest {

    @Test
    fun returns_stored_draft() {
        val draft = StockCountDraft(counts = mapOf("d1" to "10"), note = "x")
        val repo = FakeStockCountDraftRepository(initial = draft)

        val result = LoadStockCountDraftUseCase(repo).invoke(Unit).getOrThrow()

        assertEquals(draft, result)
        assertEquals(1, repo.loadCallCount)
    }

    @Test
    fun returns_empty_when_nothing_stored() {
        val repo = FakeStockCountDraftRepository()

        val result = LoadStockCountDraftUseCase(repo).invoke(Unit).getOrThrow()

        assertTrue(result.isEmpty)
    }
}

class ClearStockCountDraftUseCaseTest {

    @Test
    fun clears_stored_draft() = runTest {
        val seeded = StockCountDraft(counts = mapOf("d1" to "5"))
        val repo = FakeStockCountDraftRepository(initial = seeded)
        assertNotEquals(StockCountDraft.Empty, repo.stored)

        ClearStockCountDraftUseCase(repo, testDispatchers()).invoke().getOrThrow()

        assertEquals(1, repo.clearCallCount)
        assertEquals(StockCountDraft.Empty, repo.stored)
    }
}
