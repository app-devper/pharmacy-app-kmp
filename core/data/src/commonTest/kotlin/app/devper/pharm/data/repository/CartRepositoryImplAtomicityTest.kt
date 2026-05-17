@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.data.repository

import app.devper.pharm.data.storage.MemorySettings
import app.devper.pharm.data.storage.ParkedCartStorage
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.CartState
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.observer.CartStateProvider
import app.devper.pharm.domain.param.AddCartItemParam
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CartRepositoryImplAtomicityTest {

    private fun buildRepo(): CartRepositoryImpl =
        CartRepositoryImpl(ParkedCartStorage(MemorySettings()))

    private fun sampleDrug(id: String = "d1", name: String = "Paracetamol"): Drug =
        Drug(
            id = id,
            name = name,
            genericName = null,
            type = null,
            strength = null,
            barcode = null,
            sellPrice = 5.0,
            costPrice = 2.0,
            stock = 100,
            minStock = 0,
            unit = "เม็ด",
            regNo = null,
        )

    private fun sampleSale(id: String = "s1"): Sale =
        Sale(
            id = id,
            billNo = "BILL-001",
            total = 10.0,
            change = 0.0,
            discount = 0.0,
            stockUpdates = emptyList(),
        )

    @Test
    fun restoreCart_emits_exactly_one_tick_after_initial() = runTest(UnconfinedTestDispatcher()) {
        val repo = buildRepo()
        repo.add(AddCartItemParam(drug = sampleDrug(), altUnit = null))
        repo.parkCart(slot = 0)
        repo.commitReceipt(sampleSale())

        val captured = mutableListOf<CartState>()
        val collectorJob = launch {
            repo.state.toList(captured)
        }

        val countBefore = captured.size
        repo.restoreCart(slot = 0)
        val countAfter = captured.size

        collectorJob.cancel()

        val delta = countAfter - countBefore
        assertEquals(
            1,
            delta,
            "restoreCart should produce exactly 1 StateFlow tick, got $delta (history=${captured.map { "active=${it.active.items.size}items receipt=${it.lastReceipt != null}" }})",
        )

        val finalState = captured.last()
        assertNotNull(finalState.active.customer?.let { it } ?: finalState.active.takeIf { it.items.isNotEmpty() })
        assertNull(finalState.lastReceipt, "lastReceipt must be cleared by restoreCart in the same tick as active replacement")
        assertTrue(finalState.active.items.isNotEmpty(), "active items must be restored in the same tick")
    }

    @Test
    fun commitReceipt_emits_exactly_one_tick_after_initial() = runTest(UnconfinedTestDispatcher()) {
        val repo = buildRepo()
        repo.add(AddCartItemParam(drug = sampleDrug(), altUnit = null))

        val captured = mutableListOf<CartState>()
        val collectorJob = launch { repo.state.toList(captured) }

        val before = captured.size
        repo.commitReceipt(sampleSale())
        val after = captured.size

        collectorJob.cancel()

        assertEquals(1, after - before, "commitReceipt should produce exactly 1 tick")
        val finalState = captured.last()
        assertNotNull(finalState.lastReceipt)
        assertTrue(finalState.active.items.isEmpty(), "active items cleared atomically with receipt set")
    }

    @Test
    fun clear_emits_exactly_one_tick_after_initial() = runTest(UnconfinedTestDispatcher()) {
        val repo = buildRepo()
        repo.add(AddCartItemParam(drug = sampleDrug(), altUnit = null))
        repo.commitReceipt(sampleSale())

        val captured = mutableListOf<CartState>()
        val collectorJob = launch { repo.state.toList(captured) }

        val before = captured.size
        repo.clear()
        val after = captured.size

        collectorJob.cancel()

        assertEquals(1, after - before, "clear should produce exactly 1 tick")
        assertEquals(CartState.Empty, captured.last())
    }

    @Test
    fun CartStateProvider_never_observes_lastReceipt_null_with_old_active_during_restoreCart() = runTest(UnconfinedTestDispatcher()) {
        val repo = buildRepo()
        val oldDrug = sampleDrug(id = "old", name = "OldDrug")
        val newDrug = sampleDrug(id = "new", name = "NewDrug")

        repo.add(AddCartItemParam(drug = newDrug, altUnit = null))
        repo.parkCart(slot = 0)
        repo.add(AddCartItemParam(drug = oldDrug, altUnit = null))
        repo.commitReceipt(sampleSale())

        val provider = CartStateProvider(repo)
        val snapshots = mutableListOf<List<CartLine>>()
        val receiptStates = mutableListOf<Boolean>()
        val collectorJob = launch {
            provider.state.collect { snap ->
                snapshots.add(snap.items)
                receiptStates.add(snap.lastReceipt != null)
            }
        }

        snapshots.clear()
        receiptStates.clear()

        repo.restoreCart(slot = 0)

        collectorJob.cancel()

        for (i in snapshots.indices) {
            val items = snapshots[i]
            val hasReceipt = receiptStates[i]
            val isOldDrugVisible = items.any { it.drug.id == oldDrug.id }
            assertTrue(
                !(isOldDrugVisible && !hasReceipt),
                "Transient state observed at index $i: old active still visible (${items.map { it.drug.id }}) with receipt already cleared",
            )
        }
    }
}
