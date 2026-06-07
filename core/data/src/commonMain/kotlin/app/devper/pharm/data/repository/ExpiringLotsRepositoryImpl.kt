package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.remote.api.ExpiringLotsApi
import app.devper.pharm.data.remote.dto.ExpiringLotDto
import app.devper.pharm.data.remote.dto.WriteoffFailureDto
import app.devper.pharm.data.remote.dto.WriteoffLotsInputDto
import app.devper.pharm.data.remote.dto.WriteoffResultDto
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffFailure
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.WriteoffLotsParam
import app.devper.pharm.domain.repository.ExpiringLotsRepository

class ExpiringLotsRepositoryImpl(
    private val api: ExpiringLotsApi,
    private val stockChangeBus: StockChangeBus,
) : ExpiringLotsRepository {

    override suspend fun list(filter: ExpiringLotsFilterParam): List<ExpiringLot> =
        api.list(filter.daysAhead, filter.expiredOnly).map(::toDomain)

    override suspend fun writeoff(param: WriteoffLotsParam): WriteoffResult {
        val response = api.writeoff(WriteoffLotsInputDto(lotIds = param.lotIds))

        if (response.writtenOff > 0) stockChangeBus.emit()
        return response.toDomain()
    }

    private fun toDomain(d: ExpiringLotDto) = ExpiringLot(
        id = d.id,
        drugId = d.drugId,
        drugName = d.drugName,
        lotNumber = d.lotNumber,
        expiryDate = d.expiryDate.parseLocalDateOrNull(),
        remaining = d.remaining,
        daysLeft = d.daysLeft,
    )

    private fun WriteoffResultDto.toDomain() = WriteoffResult(
        writtenOff = writtenOff,
        failures = failed.map { it.toDomain() },
    )

    private fun WriteoffFailureDto.toDomain() = WriteoffFailure(
        lotId = lotId,
        message = error,
    )
}
