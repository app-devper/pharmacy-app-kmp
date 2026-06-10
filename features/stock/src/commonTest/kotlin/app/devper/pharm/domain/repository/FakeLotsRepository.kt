package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.inventory.LotsRepository

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.inventory.AddLotParam
import app.devper.pharm.domain.param.inventory.DeleteLotParam

class FakeLotsRepository(
    seed: List<DrugLot> = emptyList(),
    private val addThrowsOn: String? = null,
    private val deleteThrowsOn: String? = null,
    private val listThrowsOn: String? = null,
) : LotsRepository {

    private val lots: MutableList<DrugLot> = seed.toMutableList()

    var lastAdd: AddLotParam? = null
        private set
    var lastDelete: DeleteLotParam? = null
        private set
    var listCallCount: Int = 0
        private set

    override suspend fun listLots(drugId: String): List<DrugLot> {
        listCallCount++
        if (drugId == listThrowsOn) throw RuntimeException("backend rejected list: $drugId")
        return lots.filter { it.drugId == drugId }
    }

    override suspend fun addLot(param: AddLotParam): DrugLot {
        if (param.lotNumber == addThrowsOn) {
            throw RuntimeException("backend rejected lot: ${param.lotNumber}")
        }
        lastAdd = param
        val lot = DrugLot(
            id = "lot-${lots.size}",
            drugId = param.drugId,
            lotNumber = param.lotNumber,
            expiryDate = param.expiryDate,
            importDate = param.importDate ?: kotlinx.datetime.LocalDate.parse("2026-01-01"),
            costPrice = param.costPrice,
            sellPrice = param.sellPrice,
            quantity = param.quantity,
            remaining = param.quantity,
        )
        lots.add(lot)
        return lot
    }

    override suspend fun deleteLot(param: DeleteLotParam) {
        if (param.lotId == deleteThrowsOn) {
            throw RuntimeException("backend rejected delete: ${param.lotId}")
        }
        lastDelete = param
        lots.removeAll { it.id == param.lotId }
    }
}
