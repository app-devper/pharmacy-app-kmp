@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.usecase.offlinesync.RetryOfflineSaleUseCase

import app.devper.pharm.common.value.Money

import app.devper.pharm.domain.testDispatchers
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.extension.looksLikeTemporaryOutage
import app.devper.pharm.domain.repository.FakeKyRepository
import app.devper.pharm.domain.usecase.offlinesync.KyFormsNotRecordedException
import kotlinx.datetime.LocalDate
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.sales.CheckoutParam
import app.devper.pharm.domain.param.offlinesync.EnqueueOfflineSaleParam
import app.devper.pharm.domain.param.offlinesync.MarkOfflineSaleFailedParam
import app.devper.pharm.domain.param.sales.VoidSaleParam
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import app.devper.pharm.domain.repository.sales.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RetryOfflineSaleUseCaseTest {

    private fun pending(id: String) = PendingSale(
        id = id, clientRequestId = "crid-$id", payloadJson = "payload-$id", enqueuedAt = 0L,
    )

    @Test
    fun unknown_id_fails_with_not_found() = runTest {
        val queue = FakeOfflineQueue()
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(), FakeKyRepository(), testDispatchers()).invoke("missing")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertNull(queue.syncedId)
        assertNull(queue.failedParam)
    }

    @Test
    fun successful_replay_marks_synced_and_returns_sale() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1")))
        val sales = FakeReplaySales(sale = Sale("s1", "B1", Money(10.0), Money(0.0), Money(0.0), emptyList()))
        val result = RetryOfflineSaleUseCase(queue, sales, FakeKyRepository(), testDispatchers()).invoke("p1")
        assertEquals("s1", result.getOrThrow().id)
        assertEquals("payload-p1", sales.replayedPayload)
        assertEquals("p1", queue.syncedId)
        assertNull(queue.failedParam)
    }

    @Test
    fun replay_failure_marks_failed_and_rethrows() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1")))
        val boom = RuntimeException("server 500")
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(failWith = boom), FakeKyRepository(), testDispatchers()).invoke("p1")
        assertTrue(result.isFailure)
        assertEquals(boom, result.exceptionOrNull())
        assertNull(queue.syncedId)
        assertEquals("p1", queue.failedParam?.id)
        assertEquals("server 500", queue.failedParam?.error)
    }

    private fun ky10(saleId: String = "") = KyForm.Ky10(
        saleId = saleId, date = LocalDate(2026, 9, 27), drugName = "Tramadol", regNo = "R1", qty = 1,
        unit = "tab", buyerName = "A", buyerAddress = "B", rxNo = "", doctor = "", balance = 0,
    )

    private fun ky11() = KyForm.Ky11(
        saleId = "", date = LocalDate(2026, 9, 27), drugName = "Dextro", regNo = "R2", qty = 1,
        unit = "tab", buyerName = "A", purpose = "cough", pharmacist = "P",
    )

    @Test
    fun queued_ky_forms_are_sent_with_the_confirmed_sale_id() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1").copy(kyForms = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository()
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(), ky, testDispatchers()).invoke("p1")
        assertEquals("s1", result.getOrThrow().id)
        assertEquals(listOf("s1"), ky.ky10Submissions.map { it.saleId })
        assertEquals(listOf("s1"), ky.ky11Submissions.map { it.saleId })
        assertEquals("p1", queue.syncedId)
    }

    @Test
    fun outage_while_sending_ky_keeps_the_rest_on_the_bill() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1").copy(kyForms = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository(ky10Error = RuntimeException("Failed to connect to host"))
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(), ky, testDispatchers()).invoke("p1")
        assertTrue(result.exceptionOrNull()!!.looksLikeTemporaryOutage())
        assertNull(queue.syncedId)
        assertEquals(2, queue.kyForms["p1"]?.size)
        assertTrue(ky.ky11Submissions.isEmpty())
    }

    @Test
    fun refused_ky_form_stays_on_the_bill_and_marks_it_failed() = runTest {
        val queue = FakeOfflineQueue(listOf(pending("p1").copy(kyForms = listOf(ky10(), ky11()))))
        val ky = FakeKyRepository(ky10Throws = true)
        val result = RetryOfflineSaleUseCase(queue, FakeReplaySales(), ky, testDispatchers()).invoke("p1")
        assertTrue(result.exceptionOrNull() is KyFormsNotRecordedException)
        assertNull(queue.syncedId)
        assertEquals(listOf("Tramadol"), queue.kyForms["p1"]?.map { it.drugName })
        assertEquals(1, ky.ky11Submissions.size)
        assertEquals("p1", queue.failedParam?.id)
    }
}

private class FakeOfflineQueue(initial: List<PendingSale> = emptyList()) : OfflineSaleQueue {
    private val _pending = MutableStateFlow(initial)
    override val pending: StateFlow<List<PendingSale>> = _pending.asStateFlow()

    var syncedId: String? = null
        private set
    var failedParam: MarkOfflineSaleFailedParam? = null
        private set

    override fun enqueue(param: EnqueueOfflineSaleParam): String = ""
    override fun markSynced(id: String) { syncedId = id }
    override fun markFailed(param: MarkOfflineSaleFailedParam) { failedParam = param }
    val kyForms = mutableMapOf<String, List<KyForm>>()
    override fun setKyForms(id: String, forms: List<KyForm>) { kyForms[id] = forms }
    override fun clear() {}
}

private class FakeReplaySales(
    val sale: Sale = Sale("s1", "B1", Money(0.0), Money(0.0), Money(0.0), emptyList()),
    private val failWith: Throwable? = null,
) : SaleRepository {
    var replayedPayload: String? = null
        private set

    override suspend fun checkout(param: CheckoutParam): Sale = sale
    override suspend fun void(param: VoidSaleParam) {}
    override fun serializeCheckout(param: CheckoutParam): String = ""
    override suspend fun replayCheckout(payloadJson: String): Sale {
        replayedPayload = payloadJson
        failWith?.let { throw it }
        return sale
    }
}
