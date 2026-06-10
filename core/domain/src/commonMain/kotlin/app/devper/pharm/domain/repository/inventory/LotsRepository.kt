package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.inventory.AddLotParam
import app.devper.pharm.domain.param.inventory.DeleteLotParam

interface LotsRepository {
    suspend fun listLots(drugId: String): List<DrugLot>
    suspend fun addLot(param: AddLotParam): DrugLot
    suspend fun deleteLot(param: DeleteLotParam)
}
