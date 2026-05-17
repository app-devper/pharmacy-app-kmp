package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.DrugLot
import app.devper.pharm.domain.param.AddLotParam
import app.devper.pharm.domain.param.DeleteLotParam

interface LotsRepository {
    suspend fun listLots(drugId: String): List<DrugLot>
    suspend fun addLot(param: AddLotParam): DrugLot
    suspend fun deleteLot(param: DeleteLotParam)
}
