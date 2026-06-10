package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.inventory.WriteoffLotsParam

interface ExpiringLotsRepository {
    suspend fun list(filter: ExpiringLotsFilterParam): List<ExpiringLot>
    suspend fun writeoff(param: WriteoffLotsParam): WriteoffResult
}
