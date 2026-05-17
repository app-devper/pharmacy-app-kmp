package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.model.StockCountLine
import app.devper.pharm.domain.param.CreateStockCountParam

class FakeStockCountsRepository(
    private val seed: List<StockCount> = emptyList(),
    private val addThrows: Boolean = false,
) : StockCountsRepository {

    var lastAdd: CreateStockCountParam? = null
        private set
    var listCallCount: Int = 0
        private set

    override suspend fun list(limit: Int): List<StockCount> {
        listCallCount++
        return seed.take(limit)
    }

    override suspend fun add(param: CreateStockCountParam): StockCount {
        if (addThrows) throw RuntimeException("create failed")
        lastAdd = param
        return StockCount(
            id = "sc-${seed.size + 1}",
            countNo = "CN-${seed.size + 1}",
            note = param.note,
            items = param.items.map {
                StockCountLine(
                    drugId = it.drugId,
                    drugName = "",
                    unit = "",
                    systemStock = 0,
                    counted = it.counted,
                    delta = it.counted,
                )
            },
            createdAt = "2026-05-14T00:00:00Z",
        )
    }
}
