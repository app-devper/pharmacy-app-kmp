package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.ExpiringLotsApi
import app.devper.pharm.data.remote.dto.WriteoffLotsInputDto
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.WriteoffLotsParam
import app.devper.pharm.domain.repository.ExpiringLotsRepository

class ExpiringLotsRepositoryImpl(
    private val api: ExpiringLotsApi,
    private val stockChangeBus: StockChangeBus,
) : ExpiringLotsRepository {

    override suspend fun list(filter: ExpiringLotsFilterParam): List<ExpiringLot> =
        api.list(filter.daysAhead, filter.expiredOnly).map { it.toDomain() }

    override suspend fun writeoff(param: WriteoffLotsParam): WriteoffResult {
        val response = api.writeoff(WriteoffLotsInputDto(lotIds = param.lotIds))

        if (response.writtenOff > 0) stockChangeBus.emit()
        return response.toDomain()
    }
}
