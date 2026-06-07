package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.LotsApi
import app.devper.pharm.data.remote.dto.DrugLotDto
import app.devper.pharm.data.remote.dto.DrugLotInputDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.AddLotParam
import app.devper.pharm.domain.param.DeleteLotParam
import app.devper.pharm.domain.repository.LotsRepository

class LotsRepositoryImpl(
    private val api: LotsApi,
    private val stockChangeBus: StockChangeBus,
) : LotsRepository {

    override suspend fun listLots(drugId: String): List<DrugLot> =
        api.list(drugId).map(::toDomain)

    override suspend fun addLot(param: AddLotParam): DrugLot {
        val lot = toDomain(api.add(param.drugId, param.toRequest()))
        stockChangeBus.emit()
        return lot
    }

    override suspend fun deleteLot(param: DeleteLotParam) {
        api.delete(param.drugId, param.lotId)
        stockChangeBus.emit()
    }

    private fun toDomain(d: DrugLotDto) = DrugLot(
        id = d.id,
        drugId = d.drugId,
        drugName = d.drugName?.takeIf { it.isNotBlank() },
        lotNumber = d.lotNumber,
        expiryDate = d.expiryDate.parseLocalDateOrNull(),
        importDate = d.importDate.parseLocalDateOrNull(),
        costPrice = d.costPrice,
        sellPrice = d.sellPrice,
        quantity = d.quantity,
        remaining = d.remaining,
    )

    private fun AddLotParam.toRequest() = DrugLotInputDto(
        lotNumber = lotNumber,
        expiryDate = expiryDate.toIso(),
        importDate = importDate?.toIso(),
        costPrice = costPrice,
        sellPrice = sellPrice,
        quantity = quantity,
    )
}
