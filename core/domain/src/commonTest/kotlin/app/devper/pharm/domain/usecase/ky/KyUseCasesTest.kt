@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.AddKy9Param
import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.param.KyMonthFilterParam
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

class SubmitKyFormsUseCaseTest {

    @Test
    fun empty_required_returns_zero_attempted_zero_failures() = runTest {
        val repo = FakeKyRepository()

        val result = SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = KyRequired(), captured = KyCaptureFields(), date = DATE,
        )

        val r = result.getOrThrow()
        assertEquals(0, r.attempted)
        assertTrue(r.failed.isEmpty())
        assertTrue(r.allOk)
    }

    @Test
    fun submits_one_ky10_per_required_line() = runTest {
        val repo = FakeKyRepository()
        val required = KyRequired(ky10 = listOf(line(drug = drug("d1", "Drug A"), qty = 5)))
        val captured = KyCaptureFields(
            ky10BuyerName = "Mr A", ky10BuyerAddress = "BKK",
            ky10RxNo = "RX-1", ky10Doctor = "Dr A", ky10Balance = 10,
        )

        val result = SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale("s1"), required = required, captured = captured, date = DATE,
        )

        val r = result.getOrThrow()
        assertEquals(1, r.attempted)
        assertTrue(r.allOk)
        assertEquals(1, repo.ky10Submissions.size)
        val form = repo.ky10Submissions.single()
        assertEquals("s1", form.saleId)
        assertEquals("Drug A", form.drugName)
        assertEquals(5, form.qty)
        assertEquals("Mr A", form.buyerName)
        assertEquals(10, form.balance)
    }

    @Test
    fun submits_ky10_ky11_ky12_in_one_pass() = runTest {
        val repo = FakeKyRepository()
        val required = KyRequired(
            ky10 = listOf(line(drug = drug("d10", "Drug-10"))),
            ky11 = listOf(line(drug = drug("d11", "Drug-11"))),
            ky12 = listOf(line(drug = drug("d12", "Drug-12"), qty = 2)),
        )
        val captured = KyCaptureFields(
            ky10BuyerName = "A", ky11BuyerName = "B", ky12PatientName = "C",
            ky12Status = "จ่ายแล้ว",
        )

        val result = SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = required, captured = captured, date = DATE,
        )

        val r = result.getOrThrow()
        assertEquals(3, r.attempted)
        assertTrue(r.allOk)
        assertEquals(1, repo.ky10Submissions.size)
        assertEquals(1, repo.ky11Submissions.size)
        assertEquals(1, repo.ky12Submissions.size)
    }

    @Test
    fun continues_through_failures_and_collects_them() = runTest {
        val repo = FakeKyRepository(ky11Throws = true)
        val required = KyRequired(
            ky10 = listOf(line(drug = drug("d10", "Drug-10"))),
            ky11 = listOf(line(drug = drug("d11", "Drug-11"))),
        )

        val result = SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = required, captured = KyCaptureFields(), date = DATE,
        )

        val r = result.getOrThrow()
        assertEquals(2, r.attempted)
        assertEquals(1, r.failed.size)
        assertTrue(r.failed[0].contains("ขย.11"))
        assertTrue(r.failed[0].contains("Drug-11"))
        assertEquals(1, repo.ky10Submissions.size)
        assertTrue(repo.ky11Submissions.isEmpty())
    }

    @Test
    fun ky12_total_value_uses_unit_price_times_display_qty() = runTest {
        val repo = FakeKyRepository()
        val drug = drug("d1", "Drug-X")
        val required = KyRequired(ky12 = listOf(line(drug = drug, qty = 3)))

        SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = required, captured = KyCaptureFields(), date = DATE,
        ).getOrThrow()

        val form = repo.ky12Submissions.single()
        assertEquals(30.0, form.totalValue)
    }

    @Test
    fun reg_no_falls_back_to_empty_when_drug_has_none() = runTest {
        val repo = FakeKyRepository()
        val required = KyRequired(ky10 = listOf(line(drug = drug(regNo = null))))

        SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = required, captured = KyCaptureFields(), date = DATE,
        ).getOrThrow()

        assertEquals("", repo.ky10Submissions.single().regNo)
    }

    @Test
    fun unit_falls_back_to_default_when_drug_has_none() = runTest {
        val repo = FakeKyRepository()
        val unitless = drug().copy(unit = null)
        val required = KyRequired(ky10 = listOf(line(drug = unitless)))

        SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = required, captured = KyCaptureFields(), date = DATE,
        ).getOrThrow()

        assertEquals("หน่วย", repo.ky10Submissions.single().unit)
    }

    @Test
    fun all_three_failing_yields_three_errors() = runTest {
        val repo = FakeKyRepository(ky10Throws = true, ky11Throws = true, ky12Throws = true)
        val required = KyRequired(
            ky10 = listOf(line(drug = drug("d10", "A"))),
            ky11 = listOf(line(drug = drug("d11", "B"))),
            ky12 = listOf(line(drug = drug("d12", "C"))),
        )

        val r = SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = required, captured = KyCaptureFields(), date = DATE,
        ).getOrThrow()

        assertEquals(3, r.attempted)
        assertEquals(3, r.failed.size)
        assertTrue(r.failed.any { it.startsWith("ขย.10") && it.contains("A") })
        assertTrue(r.failed.any { it.startsWith("ขย.11") && it.contains("B") })
        assertTrue(r.failed.any { it.startsWith("ขย.12") && it.contains("C") })
        assertTrue(r.anyFailed)
    }

    @Test
    fun convenience_invoke_builds_param() = runTest {
        val repo = FakeKyRepository()

        val result = SubmitKyFormsUseCase(repo, testDispatchers()).invoke(
            sale = sale(), required = KyRequired(), captured = KyCaptureFields(), date = DATE,
        )

        assertNotNull(result.getOrNull())
    }
}
