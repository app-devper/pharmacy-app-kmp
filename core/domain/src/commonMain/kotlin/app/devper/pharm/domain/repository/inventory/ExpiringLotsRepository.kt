package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.WriteoffLotsParam

interface ExpiringLotsRepository {
    suspend fun list(filter: ExpiringLotsFilterParam): List<ExpiringLot>
    suspend fun writeoff(param: WriteoffLotsParam): WriteoffResult
}
