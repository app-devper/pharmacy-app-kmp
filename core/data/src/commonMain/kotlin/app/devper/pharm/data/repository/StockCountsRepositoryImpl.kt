package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.api.StockCountsApi
import app.devper.pharm.data.remote.dto.StockCountDto
import app.devper.pharm.data.remote.dto.StockCountInputDto
import app.devper.pharm.data.remote.dto.StockCountInputItemDto
import app.devper.pharm.data.remote.dto.StockCountItemDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.StockCount
import app.devper.pharm.domain.model.StockCountLine
import app.devper.pharm.domain.param.CreateStockCountParam
import app.devper.pharm.domain.repository.StockCountsRepository

class StockCountsRepositoryImpl(
    private val api: StockCountsApi,
    private val stockChangeBus: StockChangeBus,
) : StockCountsRepository {

    override suspend fun list(limit: Int): List<StockCount> =
        api.list(limit).map(::toDomain)

    override suspend fun add(param: CreateStockCountParam): StockCount {

        val result = toDomain(api.add(param.toDto()))
        stockChangeBus.emit()
        return result
    }

    private fun toDomain(d: StockCountDto) = StockCount(
        id = d.id,
        countNo = d.countNo,
        note = d.note,
        items = d.items.map(::toLine),
        createdAt = d.createdAt.parseLocalDateTimeOrNull(),
    )

    private fun toLine(d: StockCountItemDto) = StockCountLine(
        drugId = d.drugId,
        drugName = d.drugName,
        unit = d.unit,
        systemStock = d.systemStock,
        counted = d.counted,
        delta = d.delta,
    )

    private fun CreateStockCountParam.toDto() = StockCountInputDto(
        note = note.trim(),
        items = items.map { StockCountInputItemDto(drugId = it.drugId, counted = it.counted) },
    )
}
