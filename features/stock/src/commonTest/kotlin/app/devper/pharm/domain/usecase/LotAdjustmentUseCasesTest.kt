@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.inventory.AddLotUseCase
import app.devper.pharm.domain.usecase.inventory.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.inventory.DeleteLotUseCase
import app.devper.pharm.domain.usecase.inventory.GetStockAdjustmentsUseCase
import app.devper.pharm.domain.usecase.inventory.ListLotsUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.AddLotParam
import app.devper.pharm.domain.param.AddStockAdjustmentParam
import app.devper.pharm.domain.param.DeleteLotParam
import app.devper.pharm.domain.repository.FakeLotsRepository
import app.devper.pharm.domain.repository.FakeStockAdjustmentsRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun testDispatchers() = UnconfinedTestDispatcher().let { d ->
    AppDispatchers(main = d, io = d, default = d)
}

private fun lotParam(lotNumber: String = "LOT-1", qty: Int = 100) = AddLotParam(
    drugId = "d1",
    lotNumber = lotNumber,
    expiryDate = LocalDate(2027, 6, 30),
    importDate = LocalDate(2026, 5, 1),
    costPrice = Money(2.5),
    sellPrice = Money(5.0),
    quantity = Quantity(qty),
)

private fun seedLot(id: String, drugId: String = "d1") = DrugLot(
    id = id, drugId = drugId, lotNumber = "L-$id",
    expiryDate = LocalDate(2027, 6, 30),
    importDate = LocalDate(2026, 5, 1),
    costPrice = Money.Zero, sellPrice = Money.Zero,
    quantity = Quantity(10), remaining = Quantity(10),
)

class AddLotUseCaseTest {

    @Test
    fun forwards_param_and_returns_added_lot() = runTest {
        val repo = FakeLotsRepository()
        val param = lotParam(lotNumber = "LOT-A", qty = 50)

        val lot = AddLotUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastAdd)
        assertEquals("LOT-A", lot.lotNumber)
        assertEquals(Quantity(50), lot.quantity)
        assertEquals(Quantity(50), lot.remaining)
    }

    @Test
    fun backend_rejection_wraps_in_result_failure() = runTest {
        val repo = FakeLotsRepository(addThrowsOn = "BAD")
        val param = lotParam(lotNumber = "BAD")

        val result = AddLotUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertNull(repo.lastAdd)
    }
}

class DeleteLotUseCaseTest {

    @Test
    fun forwards_param_and_removes_lot() = runTest {
        val repo = FakeLotsRepository(seed = listOf(seedLot("a"), seedLot("b")))
        val param = DeleteLotParam(drugId = "d1", lotId = "a")

        DeleteLotUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastDelete)
        val remaining = repo.listLots("d1")
        assertEquals(1, remaining.size)
        assertEquals("b", remaining[0].id)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeLotsRepository(deleteThrowsOn = "x")
        val param = DeleteLotParam(drugId = "d1", lotId = "x")

        val result = DeleteLotUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class ListLotsUseCaseTest {

    @Test
    fun filters_by_drug_id() = runTest {
        val repo = FakeLotsRepository(
            seed = listOf(
                seedLot("a", drugId = "d1"),
                seedLot("b", drugId = "d2"),
                seedLot("c", drugId = "d1"),
            ),
        )

        val result = ListLotsUseCase(repo, testDispatchers()).invoke("d1").getOrThrow()

        assertEquals(2, result.size)
        assertTrue(result.all { it.drugId == "d1" })
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun unknown_drug_returns_empty_list() = runTest {
        val repo = FakeLotsRepository()

        val result = ListLotsUseCase(repo, testDispatchers()).invoke("missing").getOrThrow()

        assertEquals(emptyList(), result)
    }
}

class AddStockAdjustmentUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeStockAdjustmentsRepository()
        val param = AddStockAdjustmentParam(
            drugId = "d1", delta = -5, reason = AdjustmentReason.Damaged, note = "broken bottle",
        )

        AddStockAdjustmentUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastAdd)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeStockAdjustmentsRepository(addThrowsOn = "d-bad")
        val param = AddStockAdjustmentParam(
            drugId = "d-bad", delta = 10, reason = AdjustmentReason.Recount,
        )

        val result = AddStockAdjustmentUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class GetStockAdjustmentsUseCaseTest {

    @Test
    fun returns_seeded_list_for_drug() = runTest {
        val adjustments = listOf(
            StockAdjustment(
                id = "a1", drugId = "d1", drugName = "",
                delta = 5, before = 0, after = 5,
                reason = AdjustmentReason.Recount, note = "", at = "",
            ),
        )
        val repo = FakeStockAdjustmentsRepository(seed = mapOf("d1" to adjustments))

        val result = GetStockAdjustmentsUseCase(repo, testDispatchers()).invoke("d1")

        assertEquals(adjustments, result.getOrThrow())
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeStockAdjustmentsRepository(listThrows = true)

        val result = GetStockAdjustmentsUseCase(repo, testDispatchers()).invoke("d1")

        assertTrue(result.isFailure)
    }
}
