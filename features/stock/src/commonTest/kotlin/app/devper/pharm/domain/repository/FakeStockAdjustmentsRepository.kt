package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.inventory.StockAdjustmentsRepository

import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.inventory.AddStockAdjustmentParam

class FakeStockAdjustmentsRepository(
    private val seed: Map<String, List<StockAdjustment>> = emptyMap(),
    private val listThrows: Boolean = false,
    private val addThrowsOn: String? = null,
) : StockAdjustmentsRepository {

    var lastAdd: AddStockAdjustmentParam? = null
        private set
    var listCallCount: Int = 0
        private set
    private val mutableSeed: MutableMap<String, MutableList<StockAdjustment>> =
        seed.mapValues { it.value.toMutableList() }.toMutableMap()

    override suspend fun list(drugId: String): List<StockAdjustment> {
        listCallCount++
        if (listThrows) throw RuntimeException("list failed")
        return mutableSeed[drugId].orEmpty()
    }

    override suspend fun add(param: AddStockAdjustmentParam) {
        if (addThrowsOn != null && param.drugId == addThrowsOn) {
            throw RuntimeException("add failed for ${param.drugId}")
        }
        lastAdd = param
        val list = mutableSeed.getOrPut(param.drugId) { mutableListOf() }
        list += StockAdjustment(
            id = "adj-${list.size + 1}",
            drugId = param.drugId,
            drugName = "",
            delta = param.delta,
            before = 0,
            after = param.delta,
            reason = param.reason,
            note = param.note,
            at = "2026-05-14T00:00:00Z",
        )
    }
}
