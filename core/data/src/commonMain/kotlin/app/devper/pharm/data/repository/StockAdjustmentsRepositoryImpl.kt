package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.StockAdjustmentApi
import app.devper.pharm.data.remote.dto.StockAdjustmentDto
import app.devper.pharm.data.remote.dto.StockAdjustmentInputDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.AdjustmentReason
import app.devper.pharm.domain.model.StockAdjustment
import app.devper.pharm.domain.param.AddStockAdjustmentParam
import app.devper.pharm.domain.repository.StockAdjustmentsRepository

class StockAdjustmentsRepositoryImpl(
    private val api: StockAdjustmentApi,
    private val stockChangeBus: StockChangeBus,
) : StockAdjustmentsRepository {

    override suspend fun list(drugId: String): List<StockAdjustment> =
        api.list(drugId).map(::toDomain)

    override suspend fun add(param: AddStockAdjustmentParam) {

        api.add(param.drugId, param.toDto())
        stockChangeBus.emit()
    }

    private fun toDomain(d: StockAdjustmentDto) = StockAdjustment(
        id = d.id,
        drugId = d.drugId,
        drugName = d.drugName,
        delta = d.delta,
        before = d.before,
        after = d.after,
        reason = AdjustmentReason.fromWire(d.reason),
        note = d.note,
        at = d.createdAt,
    )

    private fun AddStockAdjustmentParam.toDto() = StockAdjustmentInputDto(
        delta = delta,
        reason = reason.wire,
        note = note.trim(),
    )
}
