@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.ky.AddKy10UseCase
import app.devper.pharm.domain.usecase.ky.AddKy11UseCase
import app.devper.pharm.domain.usecase.ky.AddKy12UseCase
import app.devper.pharm.domain.usecase.ky.AddKy9UseCase
import app.devper.pharm.domain.usecase.ky.ExportKyFormUseCase
import app.devper.pharm.domain.usecase.ky.GetKy10EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy11EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy12EntriesUseCase
import app.devper.pharm.domain.usecase.ky.GetKy9EntriesUseCase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.ky.AddKy9Param
import app.devper.pharm.domain.param.ky.ExportKyFormParam
import app.devper.pharm.domain.param.ky.KyMonthFilterParam
import app.devper.pharm.domain.repository.FakeExportRepository
import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private val DATE = LocalDate(2026, 5, 17)

private fun drug(id: String = "d1", name: String = "Pseudoephedrine", regNo: String? = "1A 1/45") = Drug(
    id = id, name = name, genericName = null, type = null, strength = null,
    barcode = null, sellPrice = Money(10.0), costPrice = Money(0.0),
    stock = Quantity(100), minStock = Quantity.Zero,
    unit = "เม็ด", regNo = regNo,
)

private fun line(drug: Drug = drug(), qty: Int = 1) = CartLine(drug = drug, qty = qty)

private fun sale(id: String = "s1") = Sale(
    id = id, billNo = "B1", total = Money(100.0), change = Money.Zero, discount = Money.Zero,
    stockUpdates = emptyList(),
)

class AddKy9UseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeKyRepository()
        val param = AddKy9Param(
            date = DATE, drugName = "Pseudoephedrine", regNo = "1A 1/45",
            unit = "เม็ด", qty = 100, pricePerUnit = 1.5,
        )

        val result = AddKy9UseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isSuccess)
        assertEquals(listOf(param), repo.ky9Adds)
    }

    @Test
    fun repository_failure_wraps_in_result() = runTest {
        val repo = FakeKyRepository(ky9Throws = true)
        val param = AddKy9Param(
            date = DATE, drugName = "Pseudoephedrine", regNo = "1A 1/45",
            unit = "เม็ด", qty = 100, pricePerUnit = 1.5,
        )

        val result = AddKy9UseCase(repo, testDispatchers()).invoke(param)

        assertTrue(result.isFailure)
        assertTrue(repo.ky9Adds.isEmpty())
    }
}

class AddKy10UseCaseTest {

    @Test
    fun forwards_form_to_repository() = runTest {
        val repo = FakeKyRepository()
        val form = KyForm.Ky10(
            saleId = "s1", date = DATE, drugName = "Phenobarbital",
            regNo = "1A 2/45", qty = 30, unit = "เม็ด",
            buyerName = "นาย ก", buyerAddress = "BKK", rxNo = "RX-1",
            doctor = "Dr A", balance = 0,
        )

        AddKy10UseCase(repo, testDispatchers()).invoke(form)

        assertEquals(listOf(form), repo.ky10Submissions)
    }
}

class AddKy11UseCaseTest {

    @Test
    fun forwards_form_to_repository() = runTest {
        val repo = FakeKyRepository()
        val form = KyForm.Ky11(
            saleId = "s1", date = DATE, drugName = "Codeine",
            regNo = "1A 3/45", qty = 50, unit = "เม็ด",
            buyerName = "นาง ข", purpose = "ไอ", pharmacist = "Pharm A",
        )

        AddKy11UseCase(repo, testDispatchers()).invoke(form)

        assertEquals(listOf(form), repo.ky11Submissions)
    }
}

class AddKy12UseCaseTest {

    @Test
    fun forwards_form_to_repository() = runTest {
        val repo = FakeKyRepository()
        val form = KyForm.Ky12(
            saleId = "s1", date = DATE, drugName = "Methadone",
            regNo = "1A 4/45", qty = 10, unit = "เม็ด",
            rxNo = "RX-2", patientName = "นาย ค", doctor = "Dr B",
            hospital = "รพ.A", totalValue = 250.0, status = "จ่ายแล้ว",
        )

        AddKy12UseCase(repo, testDispatchers()).invoke(form)

        assertEquals(listOf(form), repo.ky12Submissions)
    }
}

class GetKy9EntriesUseCaseTest {

    @Test
    fun invoke_with_no_filter_uses_default_month() = runTest {
        val repo = FakeKyRepository()

        val result = GetKy9EntriesUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isSuccess)
        assertEquals(emptyList(), result.getOrNull())
    }

    @Test
    fun invoke_with_filter_forwards_to_repository() = runTest {
        val repo = FakeKyRepository()
        val filter = KyMonthFilterParam(month = "2026-05")

        val result = GetKy9EntriesUseCase(repo, testDispatchers()).invoke(filter)

        assertTrue(result.isSuccess)
    }
}

class GetKy10EntriesUseCaseTest {

    @Test
    fun invoke_returns_repository_list() = runTest {
        val repo = FakeKyRepository()

        val result = GetKy10EntriesUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrNull())
    }
}

class GetKy11EntriesUseCaseTest {

    @Test
    fun invoke_returns_repository_list() = runTest {
        val repo = FakeKyRepository()

        val result = GetKy11EntriesUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrNull())
    }
}

class GetKy12EntriesUseCaseTest {

    @Test
    fun invoke_returns_repository_list() = runTest {
        val repo = FakeKyRepository()

        val result = GetKy12EntriesUseCase(repo, testDispatchers()).invoke()

        assertEquals(emptyList(), result.getOrNull())
    }
}

class ExportKyFormUseCaseTest {

    @Test
    fun forwards_param_to_repository() = runTest {
        val repo = FakeExportRepository(result = "/tmp/ky10-2026-05.csv")
        val param = ExportKyFormParam(form = "ky10", month = "2026-05")

        val result = ExportKyFormUseCase(repo, testDispatchers()).invoke(param)

        assertEquals("/tmp/ky10-2026-05.csv", result.getOrNull())
        assertEquals(param, repo.lastKyParam)
    }
}
