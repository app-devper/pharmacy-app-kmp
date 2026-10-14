@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.inventory.AddDrugUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.UpdateDrugUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.param.inventory.UpdateDrugParam
import app.devper.pharm.domain.repository.FakeDrugRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun addDrugParam(name: String = "Paracetamol", barcode: String = "B1") = AddDrugParam(
    name = name, barcode = barcode,
    sellPrice = Money(2.0), costPrice = Money(1.0),
    stock = Quantity.Zero, minStock = Quantity.Zero,
)

class AddDrugUseCaseTest {

    @Test
    fun forwards_param_and_returns_added_drug() = runTest {
        val repo = FakeDrugRepository()
        val param = addDrugParam(name = "Paracetamol", barcode = "BC-1")

        val result = AddDrugUseCase(repo, testDispatchers()).invoke(param)

        assertEquals(param, repo.lastAdd)
        assertEquals("Paracetamol", result.getOrThrow().name)
    }

    @Test
    fun backend_rejection_wraps_in_result_failure() = runTest {
        val repo = FakeDrugRepository(addThrowsOn = "BAD-BARCODE")
        val param = addDrugParam(barcode = "BAD-BARCODE")

        val result = AddDrugUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class UpdateDrugUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeDrugRepository()
        val param = UpdateDrugParam(
            id = "d1", name = "Paracetamol",
            sellPrice = Money(3.0), costPrice = Money(1.5),
            minStock = Quantity(10),
        )

        UpdateDrugUseCase(repo, testDispatchers()).invoke(param).getOrThrow()

        assertEquals(param, repo.lastUpdate)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeDrugRepository(updateThrowsOn = "d1")
        val param = UpdateDrugParam(
            id = "d1", name = "x",
            sellPrice = Money.Zero, costPrice = Money.Zero,
            minStock = Quantity.Zero,
        )

        val result = UpdateDrugUseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
    }
}

class GetDrugsUseCaseTest {

    @Test
    fun returns_seeded_drug_list() = runTest {
        val repo = FakeDrugRepository(seed = emptyList())

        val result = GetDrugsUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrThrow())
        assertEquals(1, repo.listCallCount)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeDrugRepository(listThrows = true)

        val result = GetDrugsUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
    }
}

